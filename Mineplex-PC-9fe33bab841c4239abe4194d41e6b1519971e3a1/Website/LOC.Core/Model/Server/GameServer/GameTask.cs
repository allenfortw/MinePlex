namespace LOC.Core.Model.Server.GameServer
{
    public class GameTask
    {
        public int GameTaskId { get; set; }

        public Account.Account Account { get; set; }

        public string TaskName { get; set; }
    }
}
