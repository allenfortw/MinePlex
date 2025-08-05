namespace LOC.Core.Model.Server.PvpServer
{
    public class CustomBuild
    {
        public int CustomBuildId { get; set; }

        public Account.Account Account { get; set; }

        public string Name { get; set; }

        public bool Active { get; set; }

        public int CustomBuildNumber { get; set; }

        public int SkillTokensBalance { get; set; }

        public int ItemTokensBalance { get; set; }

        public int PvpClassId { get; set; }

        public int SwordSkillId { get; set; }

        public int AxeSkillId { get; set; }

        public int BowSkillId { get; set; }

        public int ClassPassiveASkillId { get; set; }
        public int ClassPassiveBSkillId { get; set; }

        public int GlobalPassiveSkillId { get; set; }

        public string Slot1Material { get; set; }
        public int Slot1Amount { get; set; }

        public string Slot2Material { get; set; }
        public int Slot2Amount { get; set; }

        public string Slot3Material { get; set; }
        public int Slot3Amount { get; set; }

        public string Slot4Material { get; set; }
        public int Slot4Amount { get; set; }

        public string Slot5Material { get; set; }
        public int Slot5Amount { get; set; }

        public string Slot6Material { get; set; }
        public int Slot6Amount { get; set; }

        public string Slot7Material { get; set; }
        public int Slot7Amount { get; set; }

        public string Slot8Material { get; set; }
        public int Slot8Amount { get; set; }

        public string Slot9Material { get; set; }
        public int Slot9Amount { get; set; }
    }
}
