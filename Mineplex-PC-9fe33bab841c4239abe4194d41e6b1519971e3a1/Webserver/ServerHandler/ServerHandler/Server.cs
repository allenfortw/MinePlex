namespace ServerHandler
{
    using System;
    using System.Diagnostics;
    using System.IO;
    using System.Reflection;
    using System.Threading;
    using System.Windows.Forms;

    public class Server
    {
        private bool _shouldBeRunning;
        private Thread _watcherThread;
        private Process _serverProcess;
        private String _path;

        public Server(String path)
        {
            _path = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location) + @"\" + path;

            _watcherThread = new Thread(Watch);
            _watcherThread.Start();
        }

        public bool ShouldBeRunning()
        {
            return _shouldBeRunning;
        }

        public void ToggleRunning()
        {
            _shouldBeRunning = !_shouldBeRunning;

            if (_serverProcess == null)
            {
                try
                {
                    String memParams = "-Djava.ext.dirs=lib -Xincgc -Xmx2048M ";
                    String args = memParams + "-jar " + "\"" + _path + @"\craftbukkit-0.0.1-snapshot.jar";
                    var processInfo = new ProcessStartInfo("java.exe", args);
                    processInfo.WorkingDirectory = _path;
                    processInfo.Verb = "runas";
                    processInfo.UseShellExecute = false;

                    _serverProcess = new Process();
                    _serverProcess.StartInfo = processInfo;
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Error creating process " + ex.Message + "\n" + ex.StackTrace);
                }
            }

            if (!_shouldBeRunning)
            {
                if (!_serverProcess.HasExited)
                {
                    _serverProcess.Kill();
                }
            }
        }

        public void PrepFiles()
        {
            var worldDirectory = new DirectoryInfo(_path + @"\world");
            var worldEndDirectory = new DirectoryInfo(_path + @"\world_the_end");

            try
            {
                if (worldDirectory.Exists)
                    worldDirectory.Delete(true);

                if (worldEndDirectory.Exists)
                    worldEndDirectory.Delete(true);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message + "\n" + ex.StackTrace);
            }

            var commonDirectory = new DirectoryInfo(Directory.GetParent(Assembly.GetExecutingAssembly().Location) + @"\Common");

            var files = commonDirectory.GetFiles();

            // Copy the files and overwrite destination files if they already exist.
            foreach (var file in files)
            {
                file.CopyTo(_path + @"\" + file.Name, true);
            }
        }

        public void Start()
        {
            _serverProcess.Start();
            _serverProcess.WaitForExit();
        }

        public void Stop()
        {
            _watcherThread.Abort();
        }

        public void Watch()
        {
            while (true)
            {
                if (_shouldBeRunning)
                {
                    PrepFiles();
                    Start();
                }

                Thread.Sleep(2000);
            }
        }
    }
}
