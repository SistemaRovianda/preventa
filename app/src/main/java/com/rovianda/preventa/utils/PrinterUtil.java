package com.rovianda.preventa.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

public class PrinterUtil {

    BluetoothDevice bluetoothDevice = null;
    BluetoothSocket socket;
    OutputStream outputStream;
    BluetoothAdapter bluetoothAdapter;
    Context context;

    public PrinterUtil(Context context) {
        this.context = context;
    }

    public Set<BluetoothDevice> findDevices() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        return pairedDevices;
    }

    public boolean connectWithPrinter(BluetoothDevice bluetoothDevice) {

        if (this.bluetoothDevice != null && socket.isConnected()) {
            return true;
        }

        this.bluetoothDevice = bluetoothDevice;
        if(this.bluetoothDevice!=null) {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // uuig generico para servicios de conexion y transmision de datos (estandar)
            try {
                //Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class<?>[] {Integer.TYPE}); // prueba de obtencion de metodos de socket de dispositivo
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                // obtencion del socket
                if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                // inicio de conexion al socket
                //Toast.makeText(this.context,"CONECTADO",Toast.LENGTH_SHORT).show();
                //outputStream = socket.getOutputStream(); // setteo del output stream
                //inputStream = socket.getInputStream();   // setteo del input stream
                //beginListenForData();
                return true;
            } catch (IOException e) {
                desconect();
                return false;
            }
        }else{

            return false;
        }
        //return  true;
    }


    String value = "";
    public void IntentPrint(String txtValue)  { // metodo que inicia la conexion con la impresora modelo EC MP-2

        if(this.bluetoothDevice!=null) {
            try {
                byte[] format = {27, 33, 0};

                socket.connect();
                // format[2] = ((byte)(0x10));
                outputStream = socket.getOutputStream();

                outputStream.write(format);
                outputStream.write(txtValue.getBytes(Charset.forName("UTF-8")));
                // escribe datos en el outputs stream del socket de la impresora
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(5000);
                            outputStream.flush();
                            outputStream.close();                    // una vez escritos los datos se cierra el output stream
                            socket.close();
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                // se cierra el socket
            } catch (Exception ex) {
                value += ex.toString() + "\n" + "Except Intent print \n";
                System.out.println("ERROR: " + ex.getMessage());

            }
        }

    }

    public void desconect(){
        this.bluetoothDevice=null;
        try {
            if(this.socket!=null && this.socket.isConnected()) {
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        return this.socket.isConnected();
    }
}

