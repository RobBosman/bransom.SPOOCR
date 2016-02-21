package nl.bransom.spoocr;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SSHCommander extends AsyncTask<String, Void, String> {

  private Context context;
  private String sshHostname;
  private int sshPort;
  private String username;
  private String keyStore;
  private String keyPass;
  private Exception exception;

  public SSHCommander(final Context context) {
    this.context = context;
    sshHostname = "37.251.9.204";
    sshPort = 22720;
    username = "root";
    keyStore = "id_rsa";
    keyPass = "Lemmein00!";
  }

  @Override
  protected String doInBackground(String... params) {
    exception = null;

    if (params.length != 1) {
      exception = new IllegalArgumentException("Excpected only one param here.");
      return null;
    }
    final String command = params[0];

    InputStream keyIs = null;
    Connection conn = null;
    Session sess = null;
    InputStream stdout = null;
    try {
      // Read the private key.
      keyIs = context.getAssets().open(keyStore);
      final char[] keyChars = new char[keyIs.available()];
      new InputStreamReader(keyIs, "UTF8").read(keyChars);

      // Create a connection instance and connect
      conn = new Connection(sshHostname, sshPort);
      conn.connect();

      // Authenticate.
      if (!conn.authenticateWithPublicKey(username, keyChars, keyPass)) {
	throw new IOException("Authentication failed.");
      }

      // Create a session and execute the command.
      sess = conn.openSession();
      sess.execCommand(command);

      stdout = new StreamGobbler(sess.getStdout());
      final StringBuffer response = new StringBuffer();
      final byte[] responseBytes = new byte[1024];
      int numBytesRead;
      while (((numBytesRead = stdout.read(responseBytes)) > 0) || (stdout.available() > 0)) {
	response.append(new String(responseBytes, 0, numBytesRead, "UTF8"));
      }
      return response.toString();
    } catch (IOException e) {
      exception = e;
      return null;
    } finally {
      if (stdout != null) {
	try {
	  stdout.close();
	} catch (IOException e) {
	  // Ignore errors here.
	}
      }
      if (keyIs != null) {
	try {
	  keyIs.close();
	} catch (IOException e) {
	  // Ignore errors here.
	}
      }
      // Close the session and the connection.
      if (sess != null) {
	sess.close();
      }
      if (conn != null) {
	conn.close();
      }
    }
  }

  @Override
  protected void onPostExecute(String response) {
    if (exception != null) {
      Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(context, response.trim(), Toast.LENGTH_LONG).show();
    }
  }

}
