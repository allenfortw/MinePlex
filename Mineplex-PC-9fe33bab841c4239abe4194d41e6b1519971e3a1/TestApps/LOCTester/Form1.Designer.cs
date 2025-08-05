namespace LOCTester
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.button1 = new System.Windows.Forms.Button();
            this.txtAdmin = new System.Windows.Forms.TextBox();
            this.txtUser = new System.Windows.Forms.TextBox();
            this.txtDetails = new System.Windows.Forms.TextBox();
            this.txtReason = new System.Windows.Forms.TextBox();
            this.button2 = new System.Windows.Forms.Button();
            this.txtAccountLookup = new System.Windows.Forms.TextBox();
            this.lblBanned = new System.Windows.Forms.Label();
            this.lblBanMessage = new System.Windows.Forms.Label();
            this.txtKillerName = new System.Windows.Forms.TextBox();
            this.btnSubmitStats = new System.Windows.Forms.Button();
            this.txtVictimName = new System.Windows.Forms.TextBox();
            this.txtGamePlayerCount = new System.Windows.Forms.TextBox();
            this.txtGameLength = new System.Windows.Forms.TextBox();
            this.txtGameStarted = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.label7 = new System.Windows.Forms.Label();
            this.cmbVictimClass = new System.Windows.Forms.ComboBox();
            this.cmbKillerClass = new System.Windows.Forms.ComboBox();
            this.lblTimeToRetrieve = new System.Windows.Forms.Label();
            this.txtCount = new System.Windows.Forms.TextBox();
            this.txtServer = new System.Windows.Forms.TextBox();
            this.button3 = new System.Windows.Forms.Button();
            this.button4 = new System.Windows.Forms.Button();
            this.button5 = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // button1
            // 
            this.button1.Location = new System.Drawing.Point(12, 120);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(75, 23);
            this.button1.TabIndex = 0;
            this.button1.Text = "Ban";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // txtAdmin
            // 
            this.txtAdmin.Location = new System.Drawing.Point(12, 12);
            this.txtAdmin.Name = "txtAdmin";
            this.txtAdmin.Size = new System.Drawing.Size(75, 20);
            this.txtAdmin.TabIndex = 1;
            // 
            // txtUser
            // 
            this.txtUser.Location = new System.Drawing.Point(12, 38);
            this.txtUser.Name = "txtUser";
            this.txtUser.Size = new System.Drawing.Size(75, 20);
            this.txtUser.TabIndex = 3;
            // 
            // txtDetails
            // 
            this.txtDetails.Location = new System.Drawing.Point(12, 90);
            this.txtDetails.Name = "txtDetails";
            this.txtDetails.Size = new System.Drawing.Size(75, 20);
            this.txtDetails.TabIndex = 4;
            // 
            // txtReason
            // 
            this.txtReason.Location = new System.Drawing.Point(12, 64);
            this.txtReason.Name = "txtReason";
            this.txtReason.Size = new System.Drawing.Size(75, 20);
            this.txtReason.TabIndex = 5;
            // 
            // button2
            // 
            this.button2.Location = new System.Drawing.Point(218, 121);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(75, 23);
            this.button2.TabIndex = 6;
            this.button2.Text = "GetInfo";
            this.button2.UseVisualStyleBackColor = true;
            this.button2.Click += new System.EventHandler(this.button2_Click);
            // 
            // txtAccountLookup
            // 
            this.txtAccountLookup.Location = new System.Drawing.Point(137, 123);
            this.txtAccountLookup.Name = "txtAccountLookup";
            this.txtAccountLookup.Size = new System.Drawing.Size(75, 20);
            this.txtAccountLookup.TabIndex = 7;
            // 
            // lblBanned
            // 
            this.lblBanned.AutoSize = true;
            this.lblBanned.Location = new System.Drawing.Point(133, 38);
            this.lblBanned.Name = "lblBanned";
            this.lblBanned.Size = new System.Drawing.Size(54, 13);
            this.lblBanned.TabIndex = 8;
            this.lblBanned.Text = "lblBanned";
            // 
            // lblBanMessage
            // 
            this.lblBanMessage.AutoSize = true;
            this.lblBanMessage.Location = new System.Drawing.Point(133, 57);
            this.lblBanMessage.Name = "lblBanMessage";
            this.lblBanMessage.Size = new System.Drawing.Size(79, 13);
            this.lblBanMessage.TabIndex = 9;
            this.lblBanMessage.Text = "lblBanMessage";
            // 
            // txtKillerName
            // 
            this.txtKillerName.Location = new System.Drawing.Point(540, 90);
            this.txtKillerName.Name = "txtKillerName";
            this.txtKillerName.Size = new System.Drawing.Size(75, 20);
            this.txtKillerName.TabIndex = 10;
            // 
            // btnSubmitStats
            // 
            this.btnSubmitStats.Location = new System.Drawing.Point(540, 194);
            this.btnSubmitStats.Name = "btnSubmitStats";
            this.btnSubmitStats.Size = new System.Drawing.Size(75, 23);
            this.btnSubmitStats.TabIndex = 11;
            this.btnSubmitStats.Text = "Submit Stats";
            this.btnSubmitStats.UseVisualStyleBackColor = true;
            // 
            // txtVictimName
            // 
            this.txtVictimName.Location = new System.Drawing.Point(540, 142);
            this.txtVictimName.Name = "txtVictimName";
            this.txtVictimName.Size = new System.Drawing.Size(75, 20);
            this.txtVictimName.TabIndex = 13;
            // 
            // txtGamePlayerCount
            // 
            this.txtGamePlayerCount.Location = new System.Drawing.Point(540, 64);
            this.txtGamePlayerCount.Name = "txtGamePlayerCount";
            this.txtGamePlayerCount.Size = new System.Drawing.Size(75, 20);
            this.txtGamePlayerCount.TabIndex = 15;
            // 
            // txtGameLength
            // 
            this.txtGameLength.Location = new System.Drawing.Point(540, 38);
            this.txtGameLength.Name = "txtGameLength";
            this.txtGameLength.Size = new System.Drawing.Size(75, 20);
            this.txtGameLength.TabIndex = 16;
            // 
            // txtGameStarted
            // 
            this.txtGameStarted.Location = new System.Drawing.Point(540, 12);
            this.txtGameStarted.Name = "txtGameStarted";
            this.txtGameStarted.Size = new System.Drawing.Size(75, 20);
            this.txtGameStarted.TabIndex = 17;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(488, 15);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(41, 13);
            this.label1.TabIndex = 18;
            this.label1.Text = "Started";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(488, 41);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(40, 13);
            this.label2.TabIndex = 19;
            this.label2.Text = "Length";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(461, 67);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(67, 13);
            this.label3.TabIndex = 20;
            this.label3.Text = "Player Count";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(488, 93);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(29, 13);
            this.label4.TabIndex = 21;
            this.label4.Text = "Killer";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(488, 123);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(32, 13);
            this.label5.TabIndex = 22;
            this.label5.Text = "Class";
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(488, 149);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(35, 13);
            this.label6.TabIndex = 23;
            this.label6.Text = "Victim";
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(488, 175);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(32, 13);
            this.label7.TabIndex = 24;
            this.label7.Text = "Class";
            // 
            // cmbVictimClass
            // 
            this.cmbVictimClass.FormattingEnabled = true;
            this.cmbVictimClass.Location = new System.Drawing.Point(540, 167);
            this.cmbVictimClass.Name = "cmbVictimClass";
            this.cmbVictimClass.Size = new System.Drawing.Size(75, 21);
            this.cmbVictimClass.TabIndex = 25;
            // 
            // cmbKillerClass
            // 
            this.cmbKillerClass.FormattingEnabled = true;
            this.cmbKillerClass.Location = new System.Drawing.Point(540, 116);
            this.cmbKillerClass.Name = "cmbKillerClass";
            this.cmbKillerClass.Size = new System.Drawing.Size(75, 21);
            this.cmbKillerClass.TabIndex = 26;
            // 
            // lblTimeToRetrieve
            // 
            this.lblTimeToRetrieve.AutoSize = true;
            this.lblTimeToRetrieve.Location = new System.Drawing.Point(133, 204);
            this.lblTimeToRetrieve.Name = "lblTimeToRetrieve";
            this.lblTimeToRetrieve.Size = new System.Drawing.Size(95, 13);
            this.lblTimeToRetrieve.TabIndex = 27;
            this.lblTimeToRetrieve.Text = "Time To Retrieve :";
            this.lblTimeToRetrieve.Click += new System.EventHandler(this.label8_Click);
            // 
            // txtCount
            // 
            this.txtCount.Location = new System.Drawing.Point(136, 172);
            this.txtCount.Name = "txtCount";
            this.txtCount.Size = new System.Drawing.Size(30, 20);
            this.txtCount.TabIndex = 28;
            // 
            // txtServer
            // 
            this.txtServer.Location = new System.Drawing.Point(137, 148);
            this.txtServer.Name = "txtServer";
            this.txtServer.Size = new System.Drawing.Size(75, 20);
            this.txtServer.TabIndex = 29;
            this.txtServer.Text = "http://";
            // 
            // button3
            // 
            this.button3.Location = new System.Drawing.Point(172, 170);
            this.button3.Name = "button3";
            this.button3.Size = new System.Drawing.Size(40, 23);
            this.button3.TabIndex = 30;
            this.button3.Text = "Test";
            this.button3.UseVisualStyleBackColor = true;
            this.button3.Click += new System.EventHandler(this.button3_Click);
            // 
            // button4
            // 
            this.button4.Location = new System.Drawing.Point(218, 170);
            this.button4.Name = "button4";
            this.button4.Size = new System.Drawing.Size(74, 23);
            this.button4.TabIndex = 31;
            this.button4.Text = "TestNoArg";
            this.button4.UseVisualStyleBackColor = true;
            this.button4.Click += new System.EventHandler(this.button4_Click);
            // 
            // button5
            // 
            this.button5.Location = new System.Drawing.Point(298, 170);
            this.button5.Name = "button5";
            this.button5.Size = new System.Drawing.Size(111, 23);
            this.button5.TabIndex = 32;
            this.button5.Text = "TestNoArgNoResult";
            this.button5.UseVisualStyleBackColor = true;
            this.button5.Click += new System.EventHandler(this.button5_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(756, 262);
            this.Controls.Add(this.button5);
            this.Controls.Add(this.button4);
            this.Controls.Add(this.button3);
            this.Controls.Add(this.txtServer);
            this.Controls.Add(this.txtCount);
            this.Controls.Add(this.lblTimeToRetrieve);
            this.Controls.Add(this.cmbKillerClass);
            this.Controls.Add(this.cmbVictimClass);
            this.Controls.Add(this.label7);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.txtGameStarted);
            this.Controls.Add(this.txtGameLength);
            this.Controls.Add(this.txtGamePlayerCount);
            this.Controls.Add(this.txtVictimName);
            this.Controls.Add(this.btnSubmitStats);
            this.Controls.Add(this.txtKillerName);
            this.Controls.Add(this.lblBanMessage);
            this.Controls.Add(this.lblBanned);
            this.Controls.Add(this.txtAccountLookup);
            this.Controls.Add(this.button2);
            this.Controls.Add(this.txtReason);
            this.Controls.Add(this.txtDetails);
            this.Controls.Add(this.txtUser);
            this.Controls.Add(this.txtAdmin);
            this.Controls.Add(this.button1);
            this.Name = "Form1";
            this.Text = "Form1";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.TextBox txtAdmin;
        private System.Windows.Forms.TextBox txtUser;
        private System.Windows.Forms.TextBox txtDetails;
        private System.Windows.Forms.TextBox txtReason;
        private System.Windows.Forms.Button button2;
        private System.Windows.Forms.TextBox txtAccountLookup;
        private System.Windows.Forms.Label lblBanned;
        private System.Windows.Forms.Label lblBanMessage;
        private System.Windows.Forms.TextBox txtKillerName;
        private System.Windows.Forms.Button btnSubmitStats;
        private System.Windows.Forms.TextBox txtVictimName;
        private System.Windows.Forms.TextBox txtGamePlayerCount;
        private System.Windows.Forms.TextBox txtGameLength;
        private System.Windows.Forms.TextBox txtGameStarted;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.ComboBox cmbVictimClass;
        private System.Windows.Forms.ComboBox cmbKillerClass;
        private System.Windows.Forms.Label lblTimeToRetrieve;
        private System.Windows.Forms.TextBox txtCount;
        private System.Windows.Forms.TextBox txtServer;
        private System.Windows.Forms.Button button3;
        private System.Windows.Forms.Button button4;
        private System.Windows.Forms.Button button5;
    }
}

