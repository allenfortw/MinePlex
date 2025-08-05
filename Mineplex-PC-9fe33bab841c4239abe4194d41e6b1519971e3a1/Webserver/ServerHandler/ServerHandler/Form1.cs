namespace ServerHandler
{
    using System;
    using System.Windows.Forms;

    public partial class Form1 : Form
    {
        private readonly Server _hg1;
        private readonly Server _hg2;
        private readonly Server _hg3;
        private readonly Server _hg4;
        private readonly Server _hg5;

        public Form1()
        {
            InitializeComponent();

            _hg1 = new Server("hg1");
            _hg2 = new Server("hg2");
            _hg3 = new Server("hg3");
            _hg4 = new Server("hg4");
            _hg5 = new Server("hg5");
        }

        private void OnFormClosing(object sender, EventArgs e)
        {
            _hg1.Stop();
            _hg2.Stop();
            _hg3.Stop();
            _hg4.Stop();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            ToggleServerAndButton(_hg1, (Button)sender);
        }

        private void button2_Click(object sender, EventArgs e)
        {
            ToggleServerAndButton(_hg2, (Button)sender);
        }

        private void button3_Click(object sender, EventArgs e)
        {
            ToggleServerAndButton(_hg3, (Button)sender);
        }

        private void button4_Click(object sender, EventArgs e)
        {
            ToggleServerAndButton(_hg4, (Button)sender);
        }

        private void button5_Click(object sender, EventArgs e)
        {
            ToggleServerAndButton(_hg5, (Button) sender);
        }

        private void ToggleServerAndButton(Server server, Button button)
        {
            server.ToggleRunning();

            if (server.ShouldBeRunning())
            {
                button.Text = "Stop";
            }
            else
            {
                button.Text = "Start";
            }
        }
    }
}
