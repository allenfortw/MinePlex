namespace LOCTester
{
    using System;
    using System.Collections.Generic;
    using System.Threading;
    using System.Windows.Forms;
    using LOC.Core;
    using LOC.Core.DependencyResolution;
    using LOC.Core.Model.Account;
    using LOC.Core.Tokens;

    public partial class Form1 : Form
    {
        private AccountRepo _accountRepo;
        private int _totalLoadTime;
        private int _maxThreadCount;
        private int _threadCount;

        public Form1()
        {
            InitializeComponent();
            _accountRepo = Resolver.Current.GetService<AccountRepo>();
        }

        private void button1_Click(object sender, EventArgs e)
        {

            var ban = new Punishment();
            ban.PunishmentId = -1;
            ban.Reason = txtReason.Text;

            _accountRepo.BanAccount(ban);
        }

        private void button2_Click(object sender, EventArgs e)
        {
            _totalLoadTime = 0;
            _threadCount = 0;
            ThreadStart work = Login;
            _maxThreadCount = int.Parse(txtCount.Text);

            for (int i = 0; i < _maxThreadCount; i++)
            {
                Thread thread = new Thread(work);
                thread.Start();
            }
        }

        private void Login()
        {
            int time = Environment.TickCount;
            var account = _accountRepo.GetAccountByName(txtAccountLookup.Text, txtServer.Text);

            _totalLoadTime += Environment.TickCount - time;
            _threadCount++;

            CheckCount();
        }

        private void Test()
        {
            int time = Environment.TickCount;
            _accountRepo.Test(txtServer.Text + "/PlayerAccount/Test");

            _totalLoadTime += Environment.TickCount - time;
            _threadCount++;

            CheckCount();
        }

        private void TestNoArg()
        {
            int time = Environment.TickCount;
            _accountRepo.TestNoArg(txtServer.Text + "/PlayerAccount/TestNoArg");

            _totalLoadTime += Environment.TickCount - time;
            _threadCount++;

            CheckCount();
        }

        private void TestNoArgNoResult()
        {
            int time = Environment.TickCount;
            _accountRepo.TestNoArgNoResult(txtServer.Text + "/PlayerAccount/TestNoArgNoReturn");

            _totalLoadTime += Environment.TickCount - time;
            _threadCount++;

            CheckCount();
        }

        private void CheckCount()
        {
            if (_threadCount == _maxThreadCount)
            {
                if (lblTimeToRetrieve.InvokeRequired)
                    lblTimeToRetrieve.Invoke(new Action(() =>
                        { lblTimeToRetrieve.Text = (_totalLoadTime/_threadCount) + "ms on average for each call"; }));
            }
        }

        private void label8_Click(object sender, EventArgs e)
        {

        }

        private void button3_Click(object sender, EventArgs e)
        {
            lblTimeToRetrieve.Text = "Testing...";

            _totalLoadTime = 0;
            _threadCount = 0;
            ThreadStart work = Test;
            _maxThreadCount = int.Parse(txtCount.Text);

            for (int i = 0; i < _maxThreadCount; i++)
            {
                Thread thread = new Thread(work);
                thread.Start();
            }
        }

        private void button4_Click(object sender, EventArgs e)
        {
            lblTimeToRetrieve.Text = "Testing...";

            _totalLoadTime = 0;
            _threadCount = 0;
            ThreadStart work = TestNoArg;
            _maxThreadCount = int.Parse(txtCount.Text);

            for (int i = 0; i < _maxThreadCount; i++)
            {
                Thread thread = new Thread(work);
                thread.Start();
            }
        }

        private void button5_Click(object sender, EventArgs e)
        {
            lblTimeToRetrieve.Text = "Testing...";

            _totalLoadTime = 0;
            _threadCount = 0;
            ThreadStart work = TestNoArgNoResult;
            _maxThreadCount = int.Parse(txtCount.Text);

            for (int i = 0; i < _maxThreadCount; i++)
            {
                Thread thread = new Thread(work);
                thread.Start();
            }
        }
    }
}
