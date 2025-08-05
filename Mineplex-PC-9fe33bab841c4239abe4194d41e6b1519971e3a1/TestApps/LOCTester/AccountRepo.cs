using LOC.Core.Tokens.Client;

namespace LOCTester
{
    using System;
    using LOC.Core.Data;
    using LOC.Core.Model.Account;
    using LOC.Core.Model.GameServer;
    using LOC.Core.Tokens;

    public class AccountRepo
    {
        private readonly IRestCallJsonWrapper _restCallWrapper;

        public AccountRepo(IRestCallJsonWrapper restCallWrapper)
        {
            _restCallWrapper = restCallWrapper;
        }

        public void Test(string server)
        {
            _restCallWrapper.MakeCall("test", new Uri(server), RestCallType.Post, 300);
        }

        public void TestNoArg(string server)
        {
            _restCallWrapper.MakeCall("", new Uri(server), RestCallType.Post, 300);
        }

        public void TestNoArgNoResult(string server)
        {
            _restCallWrapper.MakeCall("", new Uri(server), RestCallType.Post, 300);
        }

        public ClientToken GetAccountByName(string name, string server)
        {
            var loginToken = new LoginRequestToken { IpAddress = "127.0.0.1", Name = name, Server = new Server { ConnectionAddress = "localhost:25565" } };
            var uri = new Uri(server + "/PlayerAccount/Login");
            var result = _restCallWrapper.MakeCall<ClientToken>(loginToken, uri, RestCallType.Post, 300);
            return result;
        }

        public void BanAccount(Punishment ban)
        {
            var uri = new Uri("http://localhost:53885" + "/PlayerAccount/Ban");
            var result = _restCallWrapper.MakeCall<Punishment>(ban, uri, RestCallType.Post, 300);
        }
    }
}
