package com.shared.storage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.shared.storage.utilities.SharedFileUtils;
import com.shared.storage.utilities.StorageUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_CODE = 100;

    private EditText editText;
    private TextView textView, filePathTextView;
    private Button writeButton, readButton;

    private String directoryName            = "AppName";
    private String fileExtension            = "txt";
    private String fileName                 = "externalFile";

    File directory = null;
    File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();
        initializeEvent();
    }

    private void initializeView() {
        editText            = findViewById(R.id.editText);
        textView            = findViewById(R.id.textView);
        writeButton         = findViewById(R.id.writeButton);
        readButton          = findViewById(R.id.readButton);
        filePathTextView    = findViewById(R.id.filePathTextView);
    }

    private void initializeEvent() {
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        /* Is Granted, Do next code */
                        write();
                    } else {
                        /* If not granted, Request for Permission */
                        requestPermission();
                    }
                } else {
                    /* Already Granted, Do next code */
                    write();
                }
            }
        });

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        /* Is Granted, Do next code */
                       read();
                    } else {
                        /* If not granted, Request for Permission */
                        requestPermission();
                    }
                } else {
                    /* Already Granted, Do next code */
                    read();
                }
            }
        });
    }

    public void write() {
        if (StorageUtils.isExternalStorageAvailableAndWriteable())
        {
            directory = SharedFileUtils.createDirectory(directoryName);
            file = SharedFileUtils.createFile(directory, fileExtension, fileName);

            String dynamicData = editText.getText().toString();
            String staticData = "username password";        /* data pass like this only */
            writeFile(file,dynamicData);
        }
    }

    public void writeFile(File file, String data){
        PrintWriter printWriter;

        try
        {
            printWriter = new PrintWriter(file);
            printWriter.println(data);
            printWriter.flush();
            printWriter.close();

            Toast.makeText(getApplicationContext(), "WRITE FILE", Toast.LENGTH_SHORT).show();
        }
        catch (IOException iOException)
        {
            iOException.printStackTrace();
        }
    }

    public void read() {
        if (StorageUtils.isExternalStorageAvailableAndWriteable())
        {
            StringBuilder stringBuffer = readFile(file);

            if (stringBuffer.length() > 0)
            {
                String username = stringBuffer.substring(0,stringBuffer.indexOf(" "));
                String password = stringBuffer.substring(stringBuffer.indexOf(" ")+1);

                textView.setText("username : "+username+"\n"+"password : "+password);

                filePathTextView.setText(""+file);

                Toast.makeText(getApplicationContext(),"READ FILE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public StringBuilder readFile(File file){
        FileReader fileReader;

        StringBuilder stringBuffer = new StringBuilder();
        String receiveString;

        try
        {
            if (file != null)
            {
                fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                receiveString = bufferedReader.readLine();
                while (receiveString!=null){
                    stringBuffer.append(receiveString);
                    receiveString = bufferedReader.readLine();
                }

                bufferedReader.close();
                fileReader.close();
            }
        }
        catch (IOException iOException)
        {
            iOException.printStackTrace();
        }

        return stringBuffer;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        write();
                    } else {
                        Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    write();
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

}