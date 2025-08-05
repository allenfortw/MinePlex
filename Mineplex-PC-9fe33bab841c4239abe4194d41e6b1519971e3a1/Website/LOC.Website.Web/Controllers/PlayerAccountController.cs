namespace LOC.Website.Web.Controllers
{
    using System;
    using System.Web.Mvc;
    using Common;
    using Common.Models;
    using Core.Tokens;
    using Core.Tokens.Client;
    using Newtonsoft.Json;

    public class PlayerAccountController : Controller
    {
        private readonly IAccountAdministrator _accountAdministrator;
        private readonly ILogger _logger;

        public PlayerAccountController(IAccountAdministrator accountAdministrator, ILogger logger)
        {
            _accountAdministrator = accountAdministrator;
            _logger = logger;
        }

        [HttpPost]
        public ActionResult GetAccountNames()
        {
            var accountNames = _accountAdministrator.GetAccountNames();

            var json = JsonConvert.SerializeObject(accountNames);
            return Content(json, "application/json");
        }

        [HttpPost]
        public ActionResult GetAccount(string name)
        {
            var account = _accountAdministrator.GetAccountByName(name);

            var json = JsonConvert.SerializeObject(account);
            return Content(json, "application/json");
        }

        [HttpPost]
        public ActionResult GetDonor(string name)
        {
            var account = _accountAdministrator.GetAccountByName(name);

            var json = JsonConvert.SerializeObject(account);
            return Content(json, "application/json");
        }

        [HttpPost]
        public ActionResult Login(LoginRequestToken loginRequest)
        {
            long time = Environment.TickCount;
            var json = JsonConvert.SerializeObject(new ClientToken(_accountAdministrator.Login(loginRequest)));
            _logger.Log("Login Debug", "Total Login : " + (Environment.TickCount - time) + "ms");
            return Content(json, "application/json");
        }

        [HttpPost]
        public ActionResult PurchaseKnownSalesPackage(PurchaseToken token)
        {
            var json = JsonConvert.SerializeObject(_accountAdministrator.PurchaseGameSalesPackage(token));
            return Content(json, "application/json");
        }

        [HttpPost]
        public ActionResult PurchaseUnknownSalesPackage(UnknownPurchaseToken token)
        {
            var json = JsonConvert.SerializeObject(_accountAdministrator.PurchaseUnknownSalesPackage(token));
            return Content(json, "application/json");
        }

        [HttpPost]
        public void AddTask(UpdateTaskToken token)
        {
            _accountAdministrator.AddTask(token);
        }

        [HttpPost]
        public void Logout(string name)
        {
            _accountAdministrator.Logout(name);
        }

        [HttpPost]
        public ActionResult AccountExists(string name)
        {
            var json = JsonConvert.SerializeObject(_accountAdministrator.AccountExists(name));
            return Content(json, "application/json");
        }

        [HttpPost]
        public ActionResult GetMatches(string name)
        {
            var json = JsonConvert.SerializeObject(_accountAdministrator.GetAllAccountNamesMatching(name));
            return Content(json, "application/json");
        }
        
        [HttpPost]
        public ActionResult Punish(PunishToken token)
        {
            var json = JsonConvert.SerializeObject(_accountAdministrator.Punish(token).ToString());
            return Content(json, "application/json");
        }

        [HttpPost]
        public ActionResult RemovePunishment(RemovePunishmentToken token)
        {
            var json = JsonConvert.SerializeObject(_accountAdministrator.RemovePunishment(token).ToString());
            return Content(json, "application/json");
        }

        [HttpPost]
        public void RemoveBan(RemovePunishmentToken token)
        {
            _accountAdministrator.RemoveBan(token);
        }

        [HttpPost]
        public void Ignore(ClientIgnoreToken token)
        {
            _accountAdministrator.Ignore(token.Name, token.IgnoredPlayer);
        }

        [HttpPost]
        public void RemoveIgnore(ClientIgnoreToken token)
        {
            _accountAdministrator.RemoveIgnore(token.Name, token.IgnoredPlayer);
        }

        [HttpPost]
        public void SaveCustomBuild(CustomBuildToken token)
        {
            _accountAdministrator.SaveCustomBuild(token);
        }

        [HttpPost]
        public ActionResult PlayerUpdate(PlayerUpdateToken token)
        {
            var account = _accountAdministrator.GetAccountByName(token.Name);
            account.Gems += token.Gems;
            account.FilterChat = token.FilterChat;

            _accountAdministrator.UpdateAccount(account);

            var json = JsonConvert.SerializeObject(token);
            return Content(json, "application/json");
        }

        [HttpPost]
        public ActionResult RankUpdate(RankUpdateToken token)
        {
            var json = JsonConvert.SerializeObject(_accountAdministrator.UpdateRank(token));
            return Content(json, "application/json");
        }


        [HttpPost]
        public ActionResult PlayerVoted(string name)
        {
            var account = _accountAdministrator.GetAccountByName(name);

            var newPoints = 0;

            if (account != null)
            {
                if (DateTime.Now.Subtract(account.LastVote) < TimeSpan.FromDays(2))
                {
                    account.VoteModifier = Math.Min(account.VoteModifier + 1, 2);
                }
                else
                {
                    account.VoteModifier = 0;
                }

                newPoints = 30 + (10*account.VoteModifier);

                account.Gems += newPoints;
                account.LastVote = DateTime.Now;

                _accountAdministrator.UpdateAccount(account);
            }

            var token = new PlayerUpdateToken { Name = name, Gems = newPoints };

            var json = JsonConvert.SerializeObject(token);
            return Content(json, "application/json");
        }

        [HttpPost]
        public void RecordDeathStat(DeathStatToken token)
        {
        }

        [HttpPost]
        public ActionResult GetPunishClient(string name)
        {
            var json = JsonConvert.SerializeObject(new ClientToken(_accountAdministrator.GetAccountByName(name)));
            return Content(json, "application/json");
        }
    }
}