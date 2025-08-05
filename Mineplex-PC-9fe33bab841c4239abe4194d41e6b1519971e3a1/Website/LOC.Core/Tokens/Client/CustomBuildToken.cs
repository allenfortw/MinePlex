namespace LOC.Core.Tokens.Client
{
    using System.Collections.Generic;
    using Model.Server.PvpServer;

    public class CustomBuildToken
    {
        public CustomBuildToken() { }

        public CustomBuildToken(CustomBuild customBuild)
        {
            CustomBuildId = customBuild.CustomBuildId;
            Name = customBuild.Name;
            Active = customBuild.Active;
            CustomBuildNumber = customBuild.CustomBuildNumber;
            SkillTokensBalance = customBuild.SkillTokensBalance;
            ItemTokensBalance = customBuild.ItemTokensBalance;
            PvpClassId = customBuild.PvpClassId;

            SwordSkillId = customBuild.SwordSkillId;
            AxeSkillId = customBuild.AxeSkillId;
            BowSkillId = customBuild.BowSkillId;

            ClassPassiveASkillId = customBuild.ClassPassiveASkillId;
            ClassPassiveBSkillId = customBuild.ClassPassiveBSkillId;

            GlobalPassiveSkillId = customBuild.GlobalPassiveSkillId;

            Slots = new List<SlotToken>();

            Slots.Add(new SlotToken { Material = customBuild.Slot1Material, Amount = customBuild.Slot1Amount});
            Slots.Add(new SlotToken { Material = customBuild.Slot2Material, Amount = customBuild.Slot2Amount });
            Slots.Add(new SlotToken { Material = customBuild.Slot3Material, Amount = customBuild.Slot3Amount });
            Slots.Add(new SlotToken { Material = customBuild.Slot4Material, Amount = customBuild.Slot4Amount });
            Slots.Add(new SlotToken { Material = customBuild.Slot5Material, Amount = customBuild.Slot5Amount });
            Slots.Add(new SlotToken { Material = customBuild.Slot6Material, Amount = customBuild.Slot6Amount });
            Slots.Add(new SlotToken { Material = customBuild.Slot7Material, Amount = customBuild.Slot7Amount });
            Slots.Add(new SlotToken { Material = customBuild.Slot8Material, Amount = customBuild.Slot8Amount });
            Slots.Add(new SlotToken { Material = customBuild.Slot9Material, Amount = customBuild.Slot9Amount });
        }

        public int CustomBuildId { get; set; }

        public string PlayerName { get; set; }

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

        public List<SlotToken> Slots { get; set; }

        public CustomBuild GetCustomBuild()
        {
            var customBuild = new CustomBuild();

            UpdateCustomBuild(customBuild);

            return customBuild;
        }

        public void UpdateCustomBuild(CustomBuild customBuild)
        {
            customBuild.Name = Name;
            customBuild.Active = Active;
            customBuild.CustomBuildNumber = CustomBuildNumber;
            customBuild.PvpClassId = PvpClassId;
            customBuild.SkillTokensBalance = SkillTokensBalance;
            customBuild.ItemTokensBalance = ItemTokensBalance;

            customBuild.SwordSkillId = SwordSkillId;
            customBuild.AxeSkillId = AxeSkillId;
            customBuild.BowSkillId = BowSkillId;

            customBuild.ClassPassiveASkillId = ClassPassiveASkillId;
            customBuild.ClassPassiveBSkillId = ClassPassiveBSkillId;

            customBuild.GlobalPassiveSkillId = GlobalPassiveSkillId;

            if (Slots != null && Slots.Count > 0)
            {
                var slots = Slots.ToArray();
                customBuild.Slot1Material = slots[0].Material;
                customBuild.Slot1Amount = slots[0].Amount;

                customBuild.Slot2Material = slots[1].Material;
                customBuild.Slot2Amount = slots[1].Amount;

                customBuild.Slot3Material = slots[2].Material;
                customBuild.Slot3Amount = slots[2].Amount;

                customBuild.Slot4Material = slots[3].Material;
                customBuild.Slot4Amount = slots[3].Amount;

                customBuild.Slot5Material = slots[4].Material;
                customBuild.Slot5Amount = slots[4].Amount;

                customBuild.Slot6Material = slots[5].Material;
                customBuild.Slot6Amount = slots[5].Amount;

                customBuild.Slot7Material = slots[6].Material;
                customBuild.Slot7Amount = slots[6].Amount;

                customBuild.Slot8Material = slots[7].Material;
                customBuild.Slot8Amount = slots[7].Amount;

                customBuild.Slot9Material = slots[8].Material;
                customBuild.Slot9Amount = slots[8].Amount;
            }
        }
    }
}
