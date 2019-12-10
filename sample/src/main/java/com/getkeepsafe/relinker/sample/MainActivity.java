package com.getkeepsafe.relinker.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.relinker.ReLinker;

import java.io.DataOutputStream;
import java.io.File;

public class MainActivity extends Activity {
    private File mLibDir;
    private File mWorkaroundDir;

    private EditText version;

    private ReLinker.Logger logcatLogger = new ReLinker.Logger() {
        @Override
        public void log(String message) {
            Log.d("ReLinker", message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLibDir = new File(getApplicationInfo().nativeLibraryDir);
        Log.d("Mainli", "mLibDir:" + mLibDir);
        mWorkaroundDir = getDir("lib", Context.MODE_PRIVATE);
        updateTree();

        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Process process = Runtime.getRuntime().exec("su");
                    final DataOutputStream stream = new DataOutputStream(process.getOutputStream());
                    stream.writeBytes("rm -r " + mLibDir.getAbsolutePath() + "\n");
                    stream.writeBytes("rm -r " + mWorkaroundDir.getAbsolutePath() + "\n");
                    stream.writeBytes("exit\n");
                    stream.flush();
                    process.waitFor();

                    updateTree();
                    Runtime.getRuntime().exit(0);
                } catch (Throwable e) {
                    Toast.makeText(MainActivity.this, "You do not have root!", Toast.LENGTH_LONG).show();
                }
            }
        });

        version = (EditText) findViewById(R.id.version);
    }

    private void call() {
        try {
            ((TextView) findViewById(R.id.text)).setText(Native.helloJni());
            updateTree();

        } catch (UnsatisfiedLinkError e) {
            final String libVersion = version.getText().toString();
            ReLinker.log(logcatLogger).recursively().loadLibrary(MainActivity.this, "hellojni", libVersion);
            ((TextView) findViewById(R.id.text)).setText(Native.helloJni());
            updateTree();
        }
    }

    private void updateTree() {
        final File[] files = mLibDir.listFiles();
        final StringBuilder builder = new StringBuilder();
        builder.append("标准lib目录中的当前文件: ");
        if (files != null) {
            for (final File file : files) {
                builder.append(file.getName()).append(", ");
            }
        }

        builder.append("\n\nReLinker lib目录中的当前文件: ");
        final File[] relinkerFiles = mWorkaroundDir.listFiles();
        if (relinkerFiles != null) {
            for (final File file : relinkerFiles) {
                builder.append(file.getName()).append(", ");
            }
        }

        ((TextView) findViewById(R.id.tree)).setText(builder.toString());
    }
}
