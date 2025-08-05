namespace LOC.Core.Tokens.Client
{
    using System.Collections.Generic;

    public class DonorToken
    {
        public int Gems { get; set; }
        public bool Donated { get; set; }
        public List<int> SalesPackages { get; set; }
        public List<string> UnknownSalesPackages { get; set; }
        public List<CustomBuildToken> CustomBuilds { get; set; }
        public List<PetToken> Pets { get; set; }
        public int PetNameTagCount { get; set; }
    }
}
