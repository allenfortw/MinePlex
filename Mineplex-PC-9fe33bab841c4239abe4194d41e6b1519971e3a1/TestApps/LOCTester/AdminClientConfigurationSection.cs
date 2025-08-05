namespace LOCTester
{
    using System.Configuration;

    class AdminClientConfigurationSection : ConfigurationSection
    {
        private const string BASE_SERVICE_URI_PROPERTY_NAME = "baseServiceUri";

        [ConfigurationProperty(BASE_SERVICE_URI_PROPERTY_NAME, IsRequired = false, DefaultValue = "http://localhost:6969")]
        public string BaseServiceUri
        {
            get { return (string)this[BASE_SERVICE_URI_PROPERTY_NAME]; }
        }
    }
}
