package com.skozlov.breed.common.helpers.ssh;

import java.io.IOException;
import java.net.URISyntaxException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public interface SshHelper {

	public abstract boolean isFailed();

	public abstract String exec(String cmd) throws JSchException,
			InterruptedException, IOException;

	public abstract String execSudo(String cmd) throws JSchException,
			InterruptedException, IOException;

	public abstract String connect() throws JSchException,
			InterruptedException, IOException;

	public abstract void disconnect() throws IOException;

	/**
	 * Function to put file on remote server via sftp
	 * 
	 * @param localPath - local relative path to the project resources
	 * @param remotePath - remote path to copy. Should include file name as well. 
	 * @throws JSchException
	 * @throws IOException
	 * @throws SftpException
	 * @throws URISyntaxException
	 */
	public abstract void putFile(String localPath, String remotePath)
			throws JSchException, IOException, SftpException,
			URISyntaxException;

}