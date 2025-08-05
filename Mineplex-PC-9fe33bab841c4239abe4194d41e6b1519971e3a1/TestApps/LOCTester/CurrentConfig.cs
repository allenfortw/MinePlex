using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace LOCTester
{
    using System.Configuration;

    public class CurrentConfig : ICurrentConfig
    {
        private readonly Configuration _configuration;

        public CurrentConfig()
        {
            _configuration = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

            var section = (AdminClientConfigurationSection)_configuration.GetSection("adminClientConfigurationSection");
            if (section == null)
                throw new ConfigurationErrorsException("Can't find the adminClientConfigurationSection in the configuration file.");

            BaseServiceUri = section.BaseServiceUri;
        }

        public string BaseServiceUri { get; private set; }

        public int MachineNumber { get; private set; }

        public string MachineSerial { get; private set; }

        public string DomainName { get; private set; }
    }
}
