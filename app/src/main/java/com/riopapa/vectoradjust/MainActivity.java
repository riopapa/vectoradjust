package com.riopapa.vectoradjust;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView tvGo, tvInp, tvOup;
    String oup;
    int p;
    int oX = 24, oY = 24, srcX, srcY;
    String str1, str2, strZ, cmd, nbr, inpCmd, outCmd, inpPath, outPath;
    float xR, yR, baseX = -1, baseY = -1, val1 = -1, val2 = -1;
    float scale;
    String org;
    String xml = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvGo = findViewById(R.id.go);
        tvInp = findViewById(R.id.txt_src);
        tvOup = findViewById(R.id.txt_dst);
        tvInp.setText(xml);
        scale = 100;
        String oneLine  = "";
        StringBuilder sbuffer = new StringBuilder();
        InputStream is = this.getResources().openRawResource(R.raw.atest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        if (is != null) {
            try {
                while ((oneLine = reader.readLine()) != null) {
                    sbuffer.append(oneLine + "\n");
                }
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            xml = sbuffer.toString();
        }
        tvOup.setText(xml);
        TextView tvScale = findViewById(R.id.scale);
        scale = Float.parseFloat(tvScale.getText().toString()) / 100f;

        String newXml = xml;
        String PATH_STR = "pathData";
        int p = newXml.indexOf(PATH_STR);
        while (p > 0) {
            int pe = p + PATH_STR.length() + 1;
            int p1 = newXml.indexOf("\"", pe + 2);
            inpPath = newXml.substring(pe + 1, p1);
            pathData();
            newXml = newXml.substring(0, pe+1) + outPath + newXml.substring(p1);
            Log.w("Path","inp="+inpPath);
            Log.w("Path","out="+outPath);
            p = p1 + 2;
            p = newXml.indexOf(PATH_STR, p);
        }
        tvOup.setText(newXml);
        try {
            FileOutputStream fileout=openFileOutput("xml.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(newXml);
            outputWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void pathData() {

        int p = 0;
        outPath = "";
        inpCmd = "";
        baseX = 0; baseY = 0;
        while (p < inpPath.length()) {
            if (inpPath.charAt(p) >= 'A') {
                inpCmd += inpPath.charAt(p);
                p++;
                while (p < inpPath.length() && inpPath.charAt(p) < 'A'
                        && inpPath.charAt(p) != '\n') {  // digit or space
                    inpCmd += inpPath.charAt(p);
                    p++;
                }
                convertOneCmd();
                outPath += outCmd;
                inpPath = inpPath.substring(p);
                inpCmd = "";
                p = 0;
            } else if (inpPath.charAt(p) <= ' ') {
                outPath += inpPath.charAt(p);
                p++;
            }
        }
    }
    
    void convertOneCmd() {
        Log.w("one line","inp="+ inpCmd);
        cmd = inpCmd.substring(0,1);

        if (cmd.equals("M")) cmd_M();
        else if (cmd.equals("m")) cmd_m();
        else if (cmd.equals("L")) cmd_L();
        else if (cmd.equals("l")) cmd_l();
        else if (cmd.equals("V")) cmd_V();
        else if (cmd.equals("H")) cmd_H();
        else if (cmd.equals("h")) cmd_h();
        else if (cmd.equals("v")) cmd_v();
        else if (cmd.equals("A")) cmd_A();
        else if (cmd.equals("C")) cmd_C();
        else if (cmd.equals("a")) cmd_a();
        else if (cmd.equals("c")) cmd_c();
        else if (cmd.equals("S")) cmd_S();
        else if (cmd.equals("s")) cmd_s();
        else if (cmd.equals("Q")) cmd_Q();
        else if (cmd.equals("q")) cmd_q();
        else if (cmd.equals("z") || cmd.equals("Z")) {
            outCmd = cmd;
            inpCmd = "";
        }
        else {
            String msg = "Invalid CMd " + cmd +" abort";
            val1 = 0;
            outCmd += msg + "\n "+outCmd + msg;
            Log.w("Abort", outCmd + " div " + (val2 / val1));
        }
    }

    @NonNull
    private void cmd_M() {  // Mxbase,ybase
        outCmd= "M";
        inpCmd = inpCmd.substring(1);
        getTwoValues();
        baseX = val1 * scale; baseY = val2 * scale;
        outCmd += fmt(baseX)+","+fmt(baseY);
        inpCmd = "";
    }
    @NonNull
    private void cmd_m() {
        outCmd = "m";
        inpCmd = inpCmd.substring(1);
        skipBlank();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            baseX += val1 * scale; baseY += val2 * scale;
            skipBlank();
            if (inpCmd.length() == 0)
                break;
        }
    }
    @NonNull
    private void cmd_C() {  // Cx1,y1, x2,y2, xBase, yBase
        outCmd = "c";
        inpCmd = inpCmd.substring(1);
        skipBlank();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt((val1 * scale - baseX) )+","+fmt((val2 * scale - baseY));
            getTwoValues();
            outCmd += fmt((val1 * scale - baseX) )+","+fmt((val2 * scale - baseY));
            getTwoValues();
            outCmd += fmt((val1 * scale - baseX) )+","+fmt((val2 * scale - baseY));
            baseX = val1 * scale; baseY = val2 * scale;
            skipBlank();
            if (inpCmd.length() == 0)
                break;
        }
    }
    @NonNull
    private void cmd_c() {
        outCmd = "c";
        inpCmd = inpCmd.substring(1);
        skipBlank();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            baseX += val1 * scale; baseY += val2 * scale;
            skipBlank();
            if (inpCmd.length() == 0)
                break;
        }
    }
    @NonNull
    private void cmd_S() {  //  x1,y1, xBase, yBase
        outCmd = "s";
        inpCmd = inpCmd.substring(1);
        skipBlank();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt((val1 * scale - baseX) )+","+fmt((val2 * scale - baseY));
            getTwoValues();
            outCmd += fmt((val1 * scale - baseX) )+","+fmt((val2 * scale - baseY));
            baseX = val1 * scale; baseY = val2 * scale;
            skipBlank();
            if (inpCmd.length() == 0)
                break;
        }
    }
    @NonNull
    private void cmd_s() {
        outCmd = "s";
        inpCmd = inpCmd.substring(1);
        skipBlank();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            baseX += val1 * scale; baseY += val2 * scale;
            skipBlank();
            if (inpCmd.length() == 0)
                break;
        }
    }
    @NonNull
    private void cmd_Q() {  //  x1,y1, xBase, yBase
        outCmd = "q";
        inpCmd = inpCmd.substring(1);
        skipBlank();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt((val1 * scale - baseX) )+","+fmt((val2 * scale - baseY));
            getTwoValues();
            outCmd += fmt((val1 * scale - baseX) )+","+fmt((val2 * scale - baseY));
            baseX = val1 * scale; baseY = val2 * scale;
            skipBlank();
            if (inpCmd.length() == 0)
                break;
        }
    }
    @NonNull
    private void cmd_q() {
        outCmd = "q";
        inpCmd = inpCmd.substring(1);
        skipBlank();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            baseX += val1 * scale; baseY += val2 * scale;
            skipBlank();
            if (inpCmd.length() == 0)
                break;
        }
    }

    @NonNull
    private void cmd_A() {
        outCmd = "a";
        inpCmd = inpCmd.substring(1);
        getTwoValues();    // rx, ry
        outCmd += str1+","+str2;;
        getOneValues();
        outCmd += str1; // rotation
        getOneValues();
        outCmd += str1; // large_arc flag
        getOneValues();
        outCmd += str1; // sweep flag
        getOneValues();
        outCmd += fmt(val1 * scale - baseX);
        baseX = val1 * scale;
        getOneValues();
        outCmd += fmt(val1 * scale - baseY);
        baseY = val1 * scale;
    }

    @NonNull
    private void cmd_a() {
        outCmd = "a";
        inpCmd = inpCmd.substring(1);
        getTwoValues();    // rx, ry
        outCmd += str1+","+str2;;
        skipBlank();
        getOneValues();
        outCmd += str1; // rotation
        getOneValues();
        outCmd += str1; // large_arc flag
        getOneValues();
        outCmd += str1; // sweep flag
        getOneValues();
        outCmd += fmt(val1 * scale);
        baseX += val1 * scale;
        getOneValues();
        outCmd += fmt(val1 * scale);
        baseY += val1 * scale;
    }

    @NonNull
    private void cmd_L() {  // Lxbase, ybase
        outCmd = "l";
        inpCmd = inpCmd.substring(1);
        while (inpCmd.length()>0 && isDigit(inpCmd.substring(0,1))) {
            getTwoValues();
            outCmd += fmt(val1 * scale - baseX)+","+fmt(val2 * scale - baseY);
            baseX = val1 * scale; baseY = val2 * scale;
            skipBlank();
        }
    }
    @NonNull
    private void cmd_l() {
        outCmd = "l";
        inpCmd = inpCmd.substring(1);
        while (inpCmd.length()>0 && isDigit(inpCmd.substring(0,1))) {
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            baseX += val1 * scale; baseY += val2 * scale;
            skipBlank();
        }
    }

    @NonNull
    private void cmd_V() {  // Vybase
        outCmd = "v";
        inpCmd = inpCmd.substring(1);
        while (inpCmd.length()>0 && isDigit(inpCmd.substring(0,1))) {
            getOneValues();
            outCmd += fmt(val1 * scale - baseY);
            baseY = val1 * scale;
        }
    }
    @NonNull
    private void cmd_v() {
        outCmd = "v";
        inpCmd = inpCmd.substring(1);
        while (inpCmd.length()>0 && isDigit(inpCmd.substring(0,1))) {
            getOneValues();
            outCmd += fmt(val1 * scale);
            baseY += val1 * scale;
        }
    }
    @NonNull
    private void cmd_H() {  // Hxbase
        outCmd = "h";
        inpCmd = inpCmd.substring(1);
        while (inpCmd.length()>0 && isDigit(inpCmd.substring(0,1))) {
            getOneValues();
            outCmd += fmt(val1 * scale - baseX);
            baseX = val1 * scale;
        }
    }
    @NonNull
    private void cmd_h() {
        outCmd = "h";
        inpCmd = inpCmd.substring(1);
        while (inpCmd.length()>0 && isDigit(inpCmd.substring(0,1))) {
            getOneValues();
            outCmd += fmt(val1 * scale);
            baseX += val1 * scale;
        }
    }

    // getXYPos
    // if normal inpCmd will be trunked after xy Position with true
    // if no comma it returns x value only with false
    @NonNull
    private boolean getTwoValues() {
        skipBlank();
        String s = inpCmd;
        str1 = getDigitStr(s);
        val1 = Float.parseFloat(str1);
        s = s.substring(str1.length());
        if (s.length() == 0) {  // only single digits
            inpCmd = "";
            return false;
        }
        while (s.charAt(0) == ' ')
            s = s.substring(1);
        if (s.charAt(0) == ',') { // y value comming
            s = s.substring(1);
            while (s.charAt(0) == ' ')
                s = s.substring(1);
            str2 = getDigitStr(s);
            val2 = Float.parseFloat(str2);
            inpCmd = s.substring(str2.length());
            return true;
        } else {
            inpCmd = s;
        }
        return false;
    }

    private void getOneValues() {
        skipComma();
        str1 = getDigitStr(inpCmd);
        val1 = Float.parseFloat(str1);
        inpCmd = inpCmd.substring(str1.length());
    }

    String getDigitStr(String inStr) {
        String p = "";
        String s = inStr;
        while (s.length() > 0) {
            String chr = s.substring(0,1);
            if (!"0123456789.-".contains(chr))
                break;
            p += chr;
            s = s.substring(1);
        }
        return p;
    }
    // inpCmd shorten to outCmd; multi blank to one blank
    void skipBlank() {
        if (inpCmd.length() == 0)
            return;
        if (inpCmd.charAt(0) == ' ')
            outCmd += " ";
        while (inpCmd.charAt(0) == ' ') {
            inpCmd = inpCmd.substring(1);
            if (inpCmd.length() == 0)
                return;
        }
        if (inpCmd.charAt(0) == '\n') {
            outCmd += "\n";
            inpCmd = inpCmd.substring(1);
            skipBlank();
        }
    }
    void skipComma() {
        if (inpCmd.length() == 0)
            return;
        skipBlank();
        if (inpCmd.charAt(0) == ',') {
            outCmd += ",";
            inpCmd = inpCmd.substring(1);
            if (inpCmd.length() == 0)
                return;
        }
        skipBlank();
        if (inpCmd.length() == 0)
            return;
        if (inpCmd.charAt(0) == '\n') {
            outCmd += "\n";
            inpCmd = inpCmd.substring(1);
            skipBlank();
        }
    }

    boolean isDigit(String chr) {
        if ("0123456789.-".contains(chr))
            return true;
        return false;
    }

    String fmt(float d) {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%.2f",d);
    }

    // ↓ ↓ ↓ P E R M I S S I O N    RELATED /////// ↓ ↓ ↓ ↓
    ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();

    private void askPermission() {
//        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (permissionsToRequest.size() != 0) {
            requestPermissions(permissionsToRequest.toArray(new String[0]),
//            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    ALL_PERMISSIONS_RESULT);
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList <String> result = new ArrayList<>();
        for (String perm : wanted) if (hasPermission(perm)) result.add(perm);
        return result;
    }
    private boolean hasPermission(String permission) {
        return (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (String perms : permissionsToRequest) {
                if (hasPermission(perms)) {
                    permissionsRejected.add(perms);
                }
            }
            if (permissionsRejected.size() > 0) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    String msg = "These permissions are mandatory for the application. Please allow access.";
                    showDialog(msg);
                }
            } else
                Toast.makeText(getApplicationContext(), "Permissions not granted.", Toast.LENGTH_LONG).show();
        }
    }
    private void showDialog(String msg) {
        showMessageOKCancel(msg,
                (dialog, which) -> requestPermissions(permissionsRejected.toArray(
                        new String[0]), ALL_PERMISSIONS_RESULT));
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

// ↑ ↑ ↑ ↑ P E R M I S S I O N    RELATED /////// ↑ ↑ ↑

}