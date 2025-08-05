namespace LOC.Website.Common.Models
{
    using System;
    using System.Collections.Generic;
    using Core.Model.Account;
    using Core.Model.Sales;
    using Core.Tokens;
    using Core.Tokens.Client;

    public interface IAccountAdministrator
    {
        List<String> GetAccountNames();
        List<Account> GetAllAccountsMatching();
        List<Account> GetAllAccountsMatching(string name);
        List<String> GetAllAccountNamesMatching(string name);
        Account GetAccountByName(string name);
        Account GetAccountById(int id);
        Account CreateAccount(string name);
        Account UpdateAccount(Account account);
        void ApplySalesPackage(SalesPackage salesPackage, int accountId, decimal gross, decimal fee);
        Account Login(LoginRequestToken loginToken);
        void Logout(string name);

        PunishmentResponse Punish(PunishToken punish);
        PunishmentResponse RemovePunishment(RemovePunishmentToken ban);

        string PurchaseGameSalesPackage(PurchaseToken token);
        bool AccountExists(string name);
        void SaveCustomBuild(CustomBuildToken token);
        void Ignore(string accountName, string ignoredPlayer);
        void RemoveIgnore(string accountName, string ignoredPlayer);
        string PurchaseUnknownSalesPackage(UnknownPurchaseToken token);
        string UpdateRank(RankUpdateToken token);
        void RemoveBan(RemovePunishmentToken token);
        void AddTask(UpdateTaskToken token);
    }
}
