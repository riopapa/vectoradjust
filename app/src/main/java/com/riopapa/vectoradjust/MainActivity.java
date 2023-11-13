package com.riopapa.vectoradjust;

import static java.lang.String.*;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView tvGo, tvOup;
    String str1, str2, cmd, inpCmd, outCmd, inpPath, outPath;
    float baseX = 0, baseY = 0, val1 = -1, val2 = -1;
    float scale= .12f, xO = 15f, yO = 15f;

    String xml = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }


        tvGo = findViewById(R.id.go);
        tvOup = findViewById(R.id.txt_dst);
        String oneLine;
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
        try {
            FileOutputStream fileout=openFileOutput("xml.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(newXml);
            outputWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        writeFile(new File(Environment.getExternalStorageDirectory(), "download"),"drawable_xml.txt", newXml);


        tvOup.setText(newXml);


    }
    public String convertDrawableXmlToString(int drawableXmlResId) {

// Create a new InputStream object for the drawable resource.
        InputStream inputStream = getResources().openRawResource(drawableXmlResId);

// Create a new BufferedReader object for the InputStream object.
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

// Read the contents of the drawable file into a string.
        String drawableContents = "";
        String line;
        while (true) {
            try {
                if (!((line = bufferedReader.readLine()) != null)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            drawableContents += line;
        }

        return drawableContents;
// Close the BufferedReader object and the InputStream object.
//        bufferedReader.close();
//        inputStream.close();

    }


    void pathData() {

        outPath = "";
        baseX = 0; baseY = 0;

        while (inpPath.length() > 1) {
            while (inpPath.charAt(0) <= ' ')
                inpPath = inpPath.substring(1);
            inpCmd = inpPath.substring(0,1);
            inpPath = inpPath.substring(1);
            int p = 0;
            while (inpPath.length() > p && inpPath.charAt(p) < 'A') {
                p++;
            }
            inpCmd += inpPath.substring(0,p);
            inpPath = inpPath.substring(p);
            convertOneCmd();
            outPath += outCmd + "\n\t\t";
        }
    }
    void convertOneCmd() {
        Log.w("one line","inp="+ inpCmd);
        cmd = inpCmd.substring(0,1);

        switch (cmd) {
            case "M":
                cmd_M();
                break;
            case "m":
                cmd_m();
                break;
            case "L":
                cmd_L();
                break;
            case "l":
                cmd_l();
                break;
            case "V":
                cmd_V();
                break;
            case "H":
                cmd_H();
                break;
            case "h":
                cmd_h();
                break;
            case "v":
                cmd_v();
                break;
            case "A":
                cmd_A();
                break;
            case "C":
                cmd_C();
                break;
            case "a":
                cmd_a();
                break;
            case "c":
                cmd_c();
                break;
            case "S":
                cmd_S();
                break;
            case "s":
                cmd_s();
                break;
            case "Q":
                cmd_Q();
                break;
            case "q":
                cmd_q();
                break;
            case "z":
            case "Z":
                outCmd = cmd;
                inpCmd = "";
                break;
            default:
                String msg = "++++ Invalid CMd " + cmd + " abort ******\n";
                val1 = 0;
                outCmd += msg + "\n " + outCmd + msg;
                break;
        }
    }

    private void cmd_M() {  // Mxbase,ybase
        outCmd= "M";
        inpCmd = inpCmd.substring(1);
        getTwoValues();
        baseX = xO + val1 * scale; baseY = yO + val2 * scale;
        outCmd += fmt(baseX)+","+fmt(baseY)+"\n\t\t";
        inpCmd = "";
    }
    private void cmd_m() {
        outCmd = "m";
        inpCmd = inpCmd.substring(1);
        skipWhite();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            baseX += val1 * scale; baseY += val2 * scale;
            skipWhite();
            if (inpCmd.length() == 0)
                break;
        }
        outCmd += "\n\t\t";
    }
    private void cmd_C() {  // Cx1,y1, x2,y2, xBase, yBase
        outCmd = "c";
        inpCmd = inpCmd.substring(1);
        skipWhite();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt(xO + val1 * scale - baseX)+","+fmt(yO + val2 * scale - baseY);
            getTwoValues();
            outCmd += fmt(xO + val1 * scale - baseX)+","+fmt(yO + val2 * scale - baseY);
            getTwoValues();
            outCmd += fmt(xO + val1 * scale - baseX)+","+fmt(yO + val2 * scale - baseY);
            baseX = xO + val1 * scale; baseY = yO + val2 * scale;
            skipWhite();
            if (inpCmd.length() == 0)
                break;
        }
    }
    private void cmd_c() {
        outCmd = "c";
        inpCmd = inpCmd.substring(1);
        skipWhite();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            baseX += val1 * scale; baseY += val2 * scale;
            skipWhite();
            if (inpCmd.length() == 0)
                break;
        }
    }
    private void cmd_S() {  //  x1,y1, xBase, yBase
        outCmd = "s";
        inpCmd = inpCmd.substring(1);
        skipWhite();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt((xO + val1 * scale - baseX) )+","+fmt((yO + val2 * scale - baseY));
            getTwoValues();
            outCmd += fmt((xO + val1 * scale - baseX) )+","+fmt((yO + val2 * scale - baseY));
            baseX = xO + val1 * scale; baseY = yO + val2 * scale;
            skipWhite();
            if (inpCmd.length() == 0)
                break;
        }
    }
    @NonNull
    private void cmd_s() {
        outCmd = "s";
        inpCmd = inpCmd.substring(1);
        skipWhite();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            baseX += val1 * scale; baseY += val2 * scale;
            skipWhite();
            if (inpCmd.length() == 0)
                break;
        }
    }
    @NonNull
    private void cmd_Q() {  //  x1,y1, xBase, yBase
        outCmd = "q";
        inpCmd = inpCmd.substring(1);
        skipWhite();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt(xO + val1 * scale - baseX) +","+fmt(yO + val2 * scale - baseY);
            getTwoValues();
            outCmd += fmt(xO + val1 * scale - baseX) +","+fmt((yO + val2 * scale - baseY));
            baseX = xO + val1 * scale; baseY = yO + val2 * scale;
            skipWhite();
            if (inpCmd.length() == 0)
                break;
        }
    }
    private void cmd_q() {
        outCmd = "q";
        inpCmd = inpCmd.substring(1);
        skipWhite();
        while (isDigit(inpCmd.substring(0,1))) {   // continue to s
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            baseX += val1 * scale; baseY += val2 * scale;
            skipWhite();
            if (inpCmd.length() == 0)
                break;
        }
    }

    @NonNull
    private void cmd_A() {  // rx, ry, xRotation, flag1, flag2, xBase, yBase
        outCmd = "a";
        inpCmd = inpCmd.substring(1);
        getTwoValues();    // rx, ry
        outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
        getOneValues();
        outCmd += str1; // rotation
        getOneValues();
        outCmd += str1; // large_arc flag
        getOneValues();
        outCmd += str1; // sweep flag
        getOneValues();
        outCmd += fmt(xO + val1 * scale - baseX);
        baseX = xO + val1 * scale;
        getOneValues();
        outCmd += fmt(yO + val1 * scale - baseY);
        baseY = yO + val1 * scale;
    }

    private void cmd_a() {
        outCmd = "a";
        inpCmd = inpCmd.substring(1);
        getTwoValues();    // rx, ry
        outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
        skipWhite();
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

    private void cmd_L() {  // Lxbase, ybase
        outCmd = "l";
        inpCmd = inpCmd.substring(1);
        while (inpCmd.length()>0 && isDigit(inpCmd.substring(0,1))) {
            getTwoValues();
            outCmd += fmt(xO + val1 * scale - baseX)+","+fmt(yO + val2 * scale - baseY);
            baseX = xO + val1 * scale; baseY = yO + val2 * scale;
            skipWhite();
        }
    }
    private void cmd_l() {
        outCmd = "l";
        inpCmd = inpCmd.substring(1);
        while (inpCmd.length()>0 && isDigit(inpCmd.substring(0,1))) {
            getTwoValues();
            outCmd += fmt(val1 * scale)+","+fmt(val2 * scale);
            baseX += val1 * scale; baseY += val2 * scale;
            skipWhite();
        }
    }

    private void cmd_V() {  // Vybase
        outCmd = "v";
        inpCmd = inpCmd.substring(1);
        while (inpCmd.length()>0 && isDigit(inpCmd.substring(0,1))) {
            getOneValues();
            outCmd += fmt(yO + val1 * scale - baseY);
            baseY = yO + val1 * scale;
        }
    }
    private void cmd_v() {
        outCmd = "v";
        inpCmd = inpCmd.substring(1);
        while (inpCmd.length()>0 && isDigit(inpCmd.substring(0,1))) {
            getOneValues();
            outCmd += fmt(val1 * scale);
            baseY += val1 * scale;
        }
    }
    private void cmd_H() {  // Hxbase
        outCmd = "h";
        inpCmd = inpCmd.substring(1);
        while (inpCmd.length()>0 && isDigit(inpCmd.substring(0,1))) {
            getOneValues();
            outCmd += fmt(xO + val1 * scale - baseX);
            baseX = xO + val1 * scale;
        }
    }
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
    private boolean getTwoValues() {
        skipWhite();
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
    void skipWhite() {
        if (inpCmd.length() == 0)
            return;
        if (inpCmd.charAt(0) == ' ' || inpCmd.charAt(0) == '\n' || inpCmd.charAt(0) == '\t')
            outCmd += " ";
        while (inpCmd.charAt(0) <= ' ') {
            inpCmd = inpCmd.substring(1);
            if (inpCmd.length() == 0)
                return;
        }
    }
    void skipComma() {
        if (inpCmd.length() == 0)
            return;
        skipWhite();
        if (inpCmd.charAt(0) == ',') {
            outCmd += ",";
            inpCmd = inpCmd.substring(1);
            if (inpCmd.length() == 0)
                return;
        }
        skipWhite();
        if (inpCmd.length() == 0)
            return;
        if (inpCmd.charAt(0) == '\n') {
            outCmd += "\n";
            inpCmd = inpCmd.substring(1);
            skipWhite();
        }
    }

    boolean isDigit(String chr) {
        return "0123456789.-".contains(chr);
    }

    String fmt(float d) {
        String s = format("%.3f",d);
        while (s.charAt(s.length()-1) == '0')
            s = s.substring(0, s.length()-1);
        if (s.charAt(s.length()-1) == '.')
            s = s.substring(0, s.length()-1);

        return s;
    }

    void writeFile(File targetFolder, String fileName, String outText) {
        try {
            File targetFile = new File(targetFolder, fileName);
            FileWriter fileWriter = new FileWriter(targetFile, false);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(outText);
            bufferedWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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