package nl.bransom.spoocr;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  public void onClickPing1(View view) {
    final String ipAddress = ((EditText) findViewById(R.id.editTextIPAddress1)).getText().toString();
    onClickPing(ipAddress);
  }

  public void onClickPing2(View view) {
    final String ipAddress = ((EditText) findViewById(R.id.editTextIPAddress2)).getText().toString();
    onClickPing(ipAddress);
  }

  public void onClickPing3(View view) {
    final String ipAddress = ((EditText) findViewById(R.id.editTextIPAddress3)).getText().toString();
    onClickPing(ipAddress);
  }

  public void onClickWake1(View view) {
    final String macAddress = ((EditText) findViewById(R.id.editTextMACAddress1)).getText().toString();
    onClickWake(macAddress);
  }

  public void onClickWake2(View view) {
    final String macAddress = ((EditText) findViewById(R.id.editTextMACAddress2)).getText().toString();
    onClickWake(macAddress);
  }

  public void onClickWake3(View view) {
    final String macAddress = ((EditText) findViewById(R.id.editTextMACAddress3)).getText().toString();
    onClickWake(macAddress);
  }

  private void onClickPing(final String ipAddress) {
    new SSHCommander(this).execute(String.format(
	"ping %s -c 1 -W 1 1> /dev/null && echo %s is online || echo %s is offline", ipAddress, ipAddress, ipAddress));
  }

  private void onClickWake(final String macAddress) {
    final String broadcastIP = "10.0.0.255";
    final int wolPort = 9;
    final int burstCount = 5;
    final int delayMillis = 200;
    final String wolCommand = String.format((Locale) null, "/usr/sbin/wol -i %s -p %d %s", broadcastIP, wolPort,
	macAddress);
    final String command;
    if (burstCount > 1) {
      command = String.format((Locale) null, "for i in `seq 1 %d`; do %s; usleep %d; done", burstCount, wolCommand,
	  delayMillis * 1000);
    } else {
      command = wolCommand;
    }
    new SSHCommander(this).execute(command);
  }
}
