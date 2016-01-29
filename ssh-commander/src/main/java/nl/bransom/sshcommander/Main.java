package nl.bransom.sshcommander;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class Main {

  public static void main(String[] args) {
    final String sshHostname = "37.251.9.204";
    final int sshPort = 22720;
    final String username = "root";
    final File keyFile = new File(System.getProperty("user.home") + "/.ssh/id_rsa");
    final String keyPass = "Lemmein00!";
    final String broadcastIP = "10.0.0.255";
    int wolPort = 9;
    String macAddress = "00:00:00:00:00:00";

    if (args != null) {
      for (String arg : args) {
	final String[] argSplit = arg.split("=");
	if (argSplit.length == 2) {
	  switch (argSplit[0].toUpperCase()) {
	  case "MAC":
	    macAddress = argSplit[1];
	    break;
	  case "PORT":
	    wolPort = Integer.valueOf(argSplit[1]);
	    break;
	  default:
	    System.err.println("Unknown argument: " + arg);
	    System.exit(-1);
	  }
	}
      }
    }

    final String command = "/usr/sbin/wol -i " + broadcastIP + " -p " + wolPort + " " + macAddress;

    Connection conn = null;
    Session sess = null;
    try {
      // Create a connection instance and connect
      conn = new Connection(sshHostname, sshPort);
      conn.connect();

      // Authenticate.
      if (!conn.authenticateWithPublicKey(username, keyFile, keyPass)) {
	throw new IOException("Authentication failed.");
      }

      // Create a session and execute the command.
      sess = conn.openSession();
      sess.execCommand(command);

      System.out.println("Here is some information about the remote host:");

      final InputStream stdout = new StreamGobbler(sess.getStdout());

      final BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
      while (br != null) {
	final String line = br.readLine();
	if (line == null) {
	  break;
	}
	System.out.println(line);
      }

      // Show exit status, if available (otherwise "null")
      System.out.println("ExitCode: " + sess.getExitStatus());

    } catch (IOException e) {
      e.printStackTrace(System.err);
      System.exit(2);
    } finally {
      // Close the session and the connection.
      if (sess != null) {
	sess.close();
      }
      if (conn != null) {
	conn.close();
      }
    }
  }

}
