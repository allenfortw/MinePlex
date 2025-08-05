namespace LOC.Website.Common.Models
{
    using System;
    using System.Collections.Generic;
    using System.Data.Entity;
    using System.Linq;
    using Core;
    using Core.Data;
    using Core.Model.Account;
    using Core.Model.Sales;
    using Core.Model.Server.GameServer;
    using Core.Model.Server.PvpServer;
    using Core.Tokens;
    using Core.Tokens.Client;
    using Data;

    public class AccountAdministrator : IAccountAdministrator
    {
        private readonly INautilusRepositoryFactory _repositoryFactory;
        private readonly IGameServerMonitor _gameServerMonitor;
        private readonly ILogger _logger;

        private readonly object _transactionLock = new object();

        public AccountAdministrator(INautilusRepositoryFactory nautilusRepositoryFactory, ILogger logger)
        {
            _repositoryFactory = nautilusRepositoryFactory;
            _gameServerMonitor = GameServerMonitor.Instance;
            _logger = logger;
        }

        public List<String> GetAccountNames()
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                return repository.GetAll<Account>().Select(x => x.Name).ToList();
            }
        }

        public List<Account> GetAllAccountsMatching()
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                return repository.GetAll<Account>().Include(x => x.Rank).ToList();
            }
        }

        public List<Account> GetAllAccountsMatching(string name)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                return repository.GetAll<Account>().Where(c => c.Name.ToUpper().Contains(name.ToUpper())).Include(x => x.Rank).ToList();
            }
        }

        public List<String> GetAllAccountNamesMatching(string name)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                return repository.GetAll<Account>().Where(c => c.Name.ToUpper().Contains(name.ToUpper())).Select(y => y.Name).ToList();
            }
        }

        public Account Login(LoginRequestToken loginToken)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.Name == loginToken.Name).FirstOrDefault() ?? CreateAccount(loginToken, repository);
                account.LoadNavigationProperties(repository.Context);
                var edited = false;

                // Expire punishments
                if (account.Punishments != null)
                {
                    foreach (var expiredPunishment in account.Punishments.Where(x => x.Active && (x.Duration - 0d) > 0 && TimeUtil.GetCurrentMilliseconds() > (x.Time + (x.Duration * 3600000))))
                    {
                        expiredPunishment.Active = false;
                        edited = true;
                    }
                }

                // Expire ranks
                if ((account.Rank.Name == "ULTRA") && !account.RankPerm && DateTime.Now.CompareTo(account.RankExpire) >= 0)
                {
                    account.Rank = repository.Where<Rank>(x => x.Name == "ALL").First();
                    repository.Attach(account.Rank);
                    edited = true;
                }

                if (edited)
                {
                    repository.CommitChanges();
                }

                return account;
            }
        }
        
        public void Logout(string name)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = GetAccountByName(name, repository);

                if (account.Logins.Any())
                {
                    account.LastLogin = account.Logins.OrderBy(x => x.Time).Last().Time;
                    account.TotalPlayingTime += DateTime.Now.Subtract(TimeSpan.FromTicks(account.LastLogin)).Ticks;
                }

                repository.CommitChanges();

                _gameServerMonitor.PlayerLoggedOut(account);
            }
        }

        public Account GetAccountByName(string name)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                return GetAccountByName(name, repository);
            }
        }

        public Account GetAccountById(int id)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                return GetAccountByName(repository.GetAll<Account>().First(x => x.AccountId == id).Name, repository);
            }
        }

        public Account CreateAccount(string name)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Add(new Account
                {
                    Gems = 1200,
                    Name = name,
                    Rank = repository.Where<Rank>(x => x.RankId == 1).First(),
                    LastVote = DateTime.Today.Subtract(TimeSpan.FromDays(5)),
                    RankExpire = DateTime.Now
                });
                repository.CommitChanges();

                return account;
            }
        }

        public Account UpdateAccount(Account account)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                repository.Edit(account);
                repository.CommitChanges();
            }

            return account;
        }

        public void AddTask(UpdateTaskToken token)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.Name == token.Name).Include(x => x.Tasks).FirstOrDefault();

                if (account != null)
                {
                    if (account.Tasks == null)
                        account.Tasks = new List<GameTask>();

                    var task = new GameTask
                        {
                            Account = account,
                            TaskName = token.NewTaskCompleted,
                        };

                    account.Tasks.Add(task);

                    repository.Edit(account);
                    repository.CommitChanges();
                }
            }
        }

        public PunishmentResponse Punish(PunishToken punish)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.Name == punish.Target).Include(x => x.Rank).FirstOrDefault();

                if (account == null)
                    return PunishmentResponse.AccountDoesNotExist;

                if (!String.Equals(punish.Admin, "Mineplex Enjin Server"))
                {
                    var punisher =
                        repository.Where<Account>(x => x.Name == punish.Admin).Include(x => x.Rank).FirstOrDefault();

                    if (punisher == null)
                        return PunishmentResponse.NotPunished;

                    if (punisher.Rank.RankId <= account.Rank.RankId)
                        return PunishmentResponse.InsufficientPrivileges;
                }

                var punishment = new Punishment
                {
                    UserId = account.AccountId,
                    Admin = punish.Admin,
                    Category = punish.Category,
                    Sentence = punish.Sentence,
                    Time = punish.Time,
                    Reason = punish.Reason,
                    Duration = punish.Duration,
                    Severity = punish.Severity,
                    Active = true
                };

                if (account.Punishments == null)
                    account.Punishments = new List<Punishment>();

                account.Punishments.Add(punishment);
                repository.Edit(account);

                repository.CommitChanges();
            }

            return PunishmentResponse.Punished;
        }

        public PunishmentResponse RemovePunishment(RemovePunishmentToken token)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.Name == token.Target).Include(x => x.Punishments).FirstOrDefault();

                if (account == null)
                    return PunishmentResponse.AccountDoesNotExist;

                if (account.Punishments == null || account.Punishments.Count == 0)
                    return PunishmentResponse.NotPunished;

                var activePunishment = account.Punishments.FirstOrDefault(x => x.PunishmentId == token.PunishmentId && x.Active);

                if (activePunishment == null)
                    return PunishmentResponse.NotPunished;

                var punishment = repository.Where<Punishment>(x => x.UserId == account.AccountId && x.PunishmentId == token.PunishmentId && x.Active).First();
                punishment.Active = false;
                punishment.Removed = true;
                punishment.RemoveAdmin = token.Admin;
                punishment.RemoveTime = DateTime.Now.Ticks;
                punishment.RemoveReason = token.Reason;

                repository.Edit(punishment);

                repository.CommitChanges();
            }

            return PunishmentResponse.PunishmentRemoved;
        }

        public string PurchaseGameSalesPackage(PurchaseToken token)
        {
            lock (_transactionLock)
            {
                try
                {
                    using (var repository = _repositoryFactory.CreateRepository())
                    {
                        var account =
                            repository.Where<Account>(x => x.Name == token.AccountName)
                                      .Include(x => x.PvpTransactions)
                                      .First();

                        var salesPackage =
                            repository.Where<GameSalesPackage>(x => x.GameSalesPackageId == token.SalesPackageId)
                                      .FirstOrDefault();

                        if (account == null || salesPackage == null)
                            return TransactionResponse.Failed.ToString();

                        if (account.Gems < salesPackage.Gems)
                            return TransactionResponse.InsufficientFunds.ToString();

                        var accountTransaction = new GameTransaction
                            {
                                Account = account,
                                GameSalesPackageId = salesPackage.GameSalesPackageId,
                                Gems = salesPackage.Gems,
                            };

                        repository.Attach(account);
                        repository.Edit(account);

                        if (account.PvpTransactions == null)
                            account.PvpTransactions = new List<GameTransaction> {accountTransaction};
                        else
                        {
                            account.PvpTransactions.Add(accountTransaction);
                        }

                        account.Gems -= salesPackage.Gems;

                        repository.CommitChanges();

                        return TransactionResponse.Success.ToString();
                    }
                }
                catch (Exception exception)
                {
                    return TransactionResponse.Failed.ToString() + ":" + exception.Message;
                }
            }
        }

        public bool AccountExists(string name)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                return repository.Any<Account>(x => x.Name == name);
            }
        }

        public void SaveCustomBuild(CustomBuildToken token)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account =
                    repository.Where<Account>(x => x.Name == token.PlayerName).Include(x => x.CustomBuilds).First();

                var customBuild =
                    account.CustomBuilds.FirstOrDefault(
                        x => x.PvpClassId == token.PvpClassId && x.CustomBuildNumber == token.CustomBuildNumber);

                if (customBuild == null)
                {
                    customBuild = repository.Add(token.GetCustomBuild());
                    account.CustomBuilds.Add(customBuild);
                }
                else
                {
                    token.UpdateCustomBuild(customBuild);
                    repository.Edit(customBuild);
                }

                if (customBuild.Active)
                {
                    foreach (
                        var otherClassBuild in
                            account.CustomBuilds.Where(
                                x =>
                                x.PvpClassId == token.PvpClassId && x.CustomBuildNumber != customBuild.CustomBuildNumber)
                                   .ToList())
                    {
                        otherClassBuild.Active = false;
                        repository.Edit(otherClassBuild);
                    }
                }
                
                repository.Edit(account);

                repository.CommitChanges();
            }
        }

        public void Ignore(string accountName, string ignoredPlayer)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.Name == accountName).First();

                account.IgnoredPlayers.Add(ignoredPlayer);

                repository.CommitChanges();
            }
        }

        public void RemoveIgnore(string accountName, string ignoredPlayer)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.Name == accountName).First();

                account.IgnoredPlayers.Remove(ignoredPlayer);

                repository.CommitChanges();
            }
        }

        public string PurchaseUnknownSalesPackage(UnknownPurchaseToken token)
        {
            lock (_transactionLock)
            {
                try
                {
                    using (var repository = _repositoryFactory.CreateRepository())
                    {
                        var account =
                            repository.Where<Account>(x => x.Name == token.AccountName)
                                      .Include(x => x.AccountTransactions)
                                      .First();

                        if (account == null)
                            return TransactionResponse.Failed.ToString();

                        if (account.Gems < token.Cost)
                            return TransactionResponse.InsufficientFunds.ToString();

                        var accountTransaction = new AccountTransaction
                        {
                            Account = account,
                            SalesPackageName = token.SalesPackageName,
                            Gems = token.Cost,
                        };

                        repository.Attach(account);
                        repository.Edit(account);

                        if (account.AccountTransactions == null)
                            account.AccountTransactions = new List<AccountTransaction> { accountTransaction };
                        else
                        {
                            account.AccountTransactions.Add(accountTransaction);
                        }

                        account.Gems -= token.Cost;
  

                        repository.CommitChanges();

                        return TransactionResponse.Success.ToString();
                    }
                }
                catch (Exception exception)
                {
                    return TransactionResponse.Failed.ToString() + ":" + exception.Message;
                }
            }
        }

        public string UpdateRank(RankUpdateToken token)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => String.Equals(x.Name, token.Name)).Include(x => x.Rank).FirstOrDefault();
                var rank = repository.Where<Rank>(x => String.Equals(x.Name, token.Rank)).FirstOrDefault();

                if (account == null)
                    return "ALL";

                if (rank == null)
                    return account.Rank.ToString();

                account.Rank = rank;
                account.RankExpire = DateTime.Now.AddMonths(1);
                account.RankPerm = token.Perm;

                repository.CommitChanges();

                return rank.ToString();
            }
        }

        public void RemoveBan(RemovePunishmentToken token)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.Name == token.Target).Include(x => x.Punishments).FirstOrDefault();

                if (account == null)
                    return;

                if (account.Punishments == null || account.Punishments.Count == 0)
                    return;

                var activePunishments = account.Punishments.Where(x => x.Active);

                if (!activePunishments.Any())
                    return;

                foreach (Punishment punishment in activePunishments)
                {
                    punishment.Active = false;
                    punishment.Removed = true;
                    punishment.RemoveAdmin = token.Admin;
                    punishment.RemoveTime = DateTime.Now.Ticks;
                    punishment.RemoveReason = token.Reason;

                    repository.Edit(punishment);
                }

                repository.CommitChanges();
            }
        }

        public void ApplySalesPackage(SalesPackage salesPackage, int accountId, decimal gross, decimal fee)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.AccountId == accountId).Include(x => x.Transactions).First();

                var accountTransaction = new Transaction {Account = account, SalesPackage = salesPackage, Fee = fee, Profit = (gross - fee), Time = DateTime.Now};

                repository.Attach(salesPackage);

                repository.Attach(account);
                repository.Edit(account);

                if (account.Transactions == null)
                    account.Transactions = new List<Transaction>();

                account.Transactions.Add(accountTransaction);
                account.Gems += salesPackage.Gems;
                account.Donated = true;
                account.RankPerm = salesPackage.RankPerm;

                if (salesPackage.Rank.RankId != 1 && !salesPackage.RankPerm)
                {
                    account.Rank = salesPackage.Rank;
                    account.RankExpire = DateTime.Now.AddDays(salesPackage.Length);
                }

                repository.CommitChanges();
            }
        }

        protected Account GetAccountByName(string name, IRepository repository)
        {
            var account = repository.Where<Account>(x => x.Name == name).FirstOrDefault();
            account.LoadNavigationProperties(repository.Context);

            return account;
        }

        protected Account CreateAccount(LoginRequestToken loginToken, IRepository repository)
        {
            var newAccount = new Account
            {
                Name = loginToken.Name,
                Rank = repository.Where<Rank>(x => x.RankId == 1).First(),
                Gems = 0,
                Transactions = new List<Transaction>(),
                PvpTransactions = new List<GameTransaction>(),
                IpAddresses = new List<LoginAddress>(),
                MacAddresses = new List<MacAddress>(),
                Logins = new List<Login>(),
                CustomBuilds = new List<CustomBuild>(),
                LastVote = DateTime.Today.Subtract(TimeSpan.FromDays(5)),
                RankExpire = DateTime.Now
            };

            newAccount = repository.Add(newAccount);
            repository.CommitChanges();

            newAccount = repository.Where<Account>(x => x.AccountId == newAccount.AccountId)
                                   .Include(x => x.Rank)
                                   .Include(x => x.Punishments)
                                   .Include(x => x.Clan)
                                   .Include(x => x.ClanRole)
                                   .Include(x => x.CustomBuilds)
                                   .Include(x => x.FishCatches)
                                   .Include(x => x.PvpTransactions)
                                   .Include(x => x.Pets)
                                   .First();

            return newAccount;
        }

        protected LoginAddress CreateIpAddress(LoginRequestToken loginToken, IRepository repository)
        {
            var newLoginAddress = new LoginAddress { Address = loginToken.IpAddress };

            newLoginAddress = repository.Add(newLoginAddress);
            repository.CommitChanges();

            return newLoginAddress;
        }

        protected void CreateMacAddress(LoginRequestToken loginToken, IRepository repository)
        {
            var newMacAddress = new MacAddress { Address = loginToken.MacAddress, Accounts = new List<Account>() };

            repository.Add(newMacAddress);
            repository.CommitChanges();
        }
    }
}