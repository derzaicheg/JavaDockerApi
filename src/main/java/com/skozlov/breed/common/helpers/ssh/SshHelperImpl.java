package com.skozlov.breed.common.helpers.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Properties;

import org.slf4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SshHelperImpl implements SshHelper {
	protected final Logger logger;

	private static final String SUDO_PASS_PPROMPT = "[sudo] password";

	private Channel channel;
	private Session session;
	private String prompt;

	private InputStream in;
	private OutputStream out;
	private PrintStream ps;

	private boolean FAILED;

	private String host;
	private String user;
	private String pwd;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skozlov.breed.common.helpers.SshHelper#isFailed()
	 */
	@Override
	public boolean isFailed() {
		return FAILED;
	}

	public void setFailed(boolean failed) {
		FAILED = failed;
	}

	private String getPrompt() {
		return prompt;
	}

	private void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public SshHelperImpl(String host, String user, String pwd,
			final Logger logger) throws JSchException, IOException {
		this.logger = logger;
		this.host = host;
		this.user = user;
		this.pwd = pwd;

	}

	private void createConnection(String channelDef) throws JSchException,
			IOException {
		JSch jsch = new JSch();
		session = jsch.getSession(this.user, this.host, 22);
		session.setPassword(this.pwd);
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		this.logger.info("*** SSH Session Created for Host : " + this.host
				+ "***\n");
		channel = session.openChannel(channelDef);

		out = channel.getOutputStream();
		ps = new PrintStream(out, true);
		in = channel.getInputStream();
		this.logger.info("*** SSH Shell Ready to Connect ***\n");
	}

	private String exec(String cmd, String pwd, String prompt)
			throws JSchException, InterruptedException, IOException {
		String result;
		result = connect();
		if (result != "" && !isFailed()) {
			result = sendCmd(cmd, pwd, prompt);
			this.logger.info("*** Ssh command was executed. Command is:\n'" + cmd
					+ "'\n and result is:\n" + result);
			disconnect();
		} else {
			result = null;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skozlov.breed.common.helpers.SshHelper#exec(java.lang.String)
	 */
	@Override
	public String exec(String cmd) throws JSchException, InterruptedException,
			IOException {
		return exec(cmd, "", "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.skozlov.breed.common.helpers.SshHelper#execSudo(java.lang.String)
	 */
	@Override
	public String execSudo(String cmd) throws JSchException,
			InterruptedException, IOException {
		return exec(cmd, this.pwd, SUDO_PASS_PPROMPT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skozlov.breed.common.helpers.SshHelper#connect()
	 */
	@Override
	public String connect() throws JSchException, InterruptedException,
			IOException {
		createConnection("shell");
		((ChannelShell) channel).setPtyType("dumb");
		channel.connect();
		this.logger.info("*** SSH Shell Connection Established ***\n");
		String res = getResponse();
		setPrompt(getEndLine(res));
		this.logger.info("*** Promt : " + getPrompt() + "***\n");
		setFailed(false);
		return res;
	}

	public String sendSudoCmd(String cmd) throws IOException,
			InterruptedException {
		return sendCmd(cmd, this.pwd, SUDO_PASS_PPROMPT);
	}

	public String sendCmd(String cmd) throws IOException, InterruptedException {
		return sendCmd(cmd, "", "");
	}

	public String sendCmd(String cmd, String passwd, String prompt)
			throws IOException, InterruptedException {
		String res = "\n";
		res = sendString(cmd);
		if (passwd != "")
			if (requiresPswd(res, prompt))
				res = sendString(passwd);
			else
				res = trimCmd(res, cmd);
		else
			res = trimCmd(res, cmd);
		if (ready4Next(res)) {
			setFailed(false);
			res = trimPrompt(res);
		} else {
			setFailed(true);
		}
		return res.trim();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skozlov.breed.common.helpers.SshHelper#disconnect()
	 */
	@Override
	public void disconnect() throws IOException {
		ps.close();
		out.close();
		in.close();
		channel.disconnect();
		session.disconnect();
		this.logger.info("*** Server Disconnected ***\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skozlov.breed.common.helpers.SshHelper#putFile(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void putFile(String localPath, String remotePath)
			throws JSchException, IOException, SftpException,
			URISyntaxException {
		createConnection("sftp");
		channel.connect();
		ChannelSftp c = (ChannelSftp) channel;
		try (final InputStream input = getClass().getClassLoader()
				.getResourceAsStream(localPath)) {
			c.put(input, remotePath);
		}
		try {
			c.quit();
		} catch (Exception e) {
			logger.error("Unable to disconnect from FTPserver. " + e.toString());
		}
		disconnect();

	}

	private String sendString(String cmd) throws IOException,
			InterruptedException {
		String res = "\n";
		out.write((cmd + "\n").getBytes());
		out.flush();
		res = getResponse();
		return res;
	}

	private String trimPrompt(String msg) {
		return (msg.substring(0, msg.lastIndexOf("\n") - 1));
	}

	private boolean requiresPswd(String msg, String prompt) {
		msg = getEndLine(msg);
		if (msg.startsWith(prompt))
			return true;
		return false;
	}

	private String trimCmd(String msg, String cmd) {
		return (msg.substring(cmd.length()));
	}

	private boolean ready4Next(String msg) {
		return requiresPswd(msg, prompt);
	}

	private String getEndLine(String msg) {
		String res = "";
		res = msg.substring(msg.lastIndexOf("\n") + 1);
		return res;
	}

	private String getResponse() throws InterruptedException, IOException {
		Thread.sleep(1000);
		byte[] bt = new byte[1024];
		String result = "";
		while (in.available() > 0) {
			String str;
			int i = in.read(bt, 0, 1024);
			if (i < 0)
				break;
			str = new String(bt, 0, i);
			result = result + str;
			Thread.sleep(1000);
		}
		if (result != "") {
			this.logger.info("*** Retrieved Result : "
					+ result.substring(0, 6) + "...."
					+ result.substring(result.length() - 6) + "\n");
		} else {
			this.logger.info("*** No Result to be Retrieved" + "***\n");
		}
		return result;
	}

	// public static void main(String[] args) throws JSchException, IOException,
	// InterruptedException, SftpException, URISyntaxException {
	// Logger logger = null;
	// SshHelper s = new SshHelper(logger);
	// s.putFile();
	//
	// s.connect();
	// //String cmd = s.sendCmd("pwd");
	// String result = s.sendSudoCmd("sudo apt-get install docker");
	// System.out.println("========" + result);
	// s.disconnect();
	//
	// }
}
