package com.templateapp.cloudapi.presentation;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;

public class UDP_Client
{

    private InetAddress IPAddress = null;
    private String message = "Hello Android!" ;
    private AsyncTask<Void, Void, Void> async_cient;
    public String Message;
    private TextView data;

    @SuppressLint("NewApi")
    public void NachrichtSenden()
    {
        async_cient = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                DatagramSocket ds = null;

                try
                {
                    byte[] ipAddr = new byte[]{ (byte) 192, (byte) 168,(byte)64, (byte) 255};
                    InetAddress addr = InetAddress.getByAddress(ipAddr);
                    ds = new DatagramSocket(3000);
                    DatagramPacket dp;
                    dp = new DatagramPacket(Message.getBytes(), Message.getBytes().length, addr, 3000);
                    ds.setBroadcast(true);
                    ds.send(dp);


                    byte[] buffer = new byte[2048];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    while (true) {

                        ds.receive(packet);
                        String lText = new String(buffer, 0, packet.getLength());
                        System.out.println("UDP packet received" + lText);
                        data.setText(lText);

                        packet.setLength(buffer.length);
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (ds != null)
                    {
                        ds.close();
                    }
                }
                return null;
            }

            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
            }
        };

        if (Build.VERSION.SDK_INT >= 11) async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async_cient.execute();
    }
}