package com.sarahdev.mymapapplication.persistance;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.sarahdev.mymapapplication.model.Calculs;
import com.sarahdev.mymapapplication.model.Position;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Files {
    private final static String FILESINFO="trajetsInfo.txt";
    private final static String TMPFILESINFO="trajetsInfoTmp.txt";
    private final static String FILENAME="trajet.txt";
    private final static String TMPFILENAME = "trajetTmp.txt";

    public static synchronized void addFileInfos(final Context context, String filename, String infos, double averageSpeed, double distance){
        List<String> infosList = new ArrayList<>();
        infosList.add(infos);
        infosList.add(String.valueOf(Calculs.getOneDecimalFloat(averageSpeed)));
        infosList.add(String.valueOf(distance));
        infosList.add(""); // place for child item button in expandable list
        Map fileInfos = readFileInfos(context);
        fileInfos.put(filename, infosList);
        try {
            FileOutputStream fos = context.openFileOutput(TMPFILESINFO, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(fileInfos);
            oos.close();
            fos.close();
            renameAppFile(context, TMPFILESINFO, FILESINFO);
        } catch (IOException e) {
        }
    }

    public static synchronized void updateFileInfos(final Context context, Map<String, List<String>> filesInfos){
        try {
            FileOutputStream fos = context.openFileOutput(TMPFILESINFO, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(filesInfos);
            oos.close();
            fos.close();
            renameAppFile(context,TMPFILESINFO, FILESINFO );
        } catch (IOException e) {
        }
    }

    public static synchronized Map<String, List<String>> readFileInfos(final Context context){
        Map<String, List<String>> fileInfos = new HashMap<> ();
        try {
            FileInputStream fis = context.openFileInput(FILESINFO);
            ObjectInputStream ois = new ObjectInputStream(fis);
            fileInfos = (Map<String, List<String>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return fileInfos;
    }

    public static synchronized void automaticBackup(final Context context, final List<Position> positions) {
        try {
            FileOutputStream fos = context.openFileOutput(TMPFILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(positions);
            oos.close();
            fos.close();
            renameAppFile(context, TMPFILENAME, FILENAME);
        } catch (IOException e) {
        }
    }

    public static synchronized List<Position> readTrajet(final Context context){
        List<Position> positions = new ArrayList<Position>() ;
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            positions = (List<Position>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return positions;
    }

    public static synchronized LatLng getStart(final Context context, String filename){
        List<Position> positions = new ArrayList<Position>() ;
        LatLng start = null;
        try {
            FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            positions = (List<Position>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        if (positions !=null && positions.size()>0) {
            start = positions.get(0).getLatLng();
        }
        return start;
    }

    public static synchronized List<File> savedFiles(final Context context){
        List<File> files = new ArrayList<File>() ;
        String path = context.getFilesDir().toString();
        File directory = new File(path);
        File[] allFiles = directory.listFiles();
        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i].getName().contains(FILENAME) && !allFiles[i].getName().equals(FILENAME)) {
                files.add(allFiles[i]);
            }
        }
        return files;
    }

    public static synchronized List<Position> readSavedFiles(final Context context){
        List<Position> tmpPositions = new ArrayList<Position>() ;
        List<Position> positions = new ArrayList<Position>() ;
        try {
            String path = context.getFilesDir().toString();
            File directory = new File(path);
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains("_" + FILENAME)) {
                    FileInputStream fis = context.openFileInput(files[i].getName());
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    tmpPositions = (List<Position>) ois.readObject();
                    positions.addAll(tmpPositions);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
        }
        return positions;
    }

    public static synchronized List<Position> readFile(final Context context, String filename){
        List<Position> positions = new ArrayList<Position>() ;
        try {
            FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            positions = (List<Position>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return positions;
    }

    public static synchronized void writeTrajet(final Context context, final List<Position> positions) {
        try {
            FileOutputStream fos = context.openFileOutput(TMPFILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(positions);
            oos.close();
            fos.close();
            renameAppFile(context,TMPFILENAME, FILENAME );
        } catch (IOException e) {
        }
    }

    public static synchronized void saveTrajet(final Context context, final List<Position> positions, String infos, String date, double averageSpeed, double distance) {
        String filename = date + "_" + FILENAME;
        try {
            FileOutputStream fos = context.openFileOutput(TMPFILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(positions);
            oos.close();
            fos.close();
            renameAppFile(context, TMPFILENAME, filename);
        } catch (IOException e) {
        }
        addFileInfos(context, filename , infos, averageSpeed, distance);
    }


    public static synchronized void resetTrajet(final Context context) {
        writeTrajet(context, new ArrayList<>());
    }

    public static synchronized void renameAppFile(final Context context, String tmpFilename, String filename) {
        File originalFile = context.getFileStreamPath(tmpFilename);
        File newFile = new File(originalFile.getParent(), filename);
        if (newFile.exists()) {
            context.deleteFile(filename);
        }
        originalFile.renameTo(newFile);
    }

    public static synchronized void deleteFile(final Context context, String filename) {
        if (context.getFileStreamPath(filename).exists()) {
            context.deleteFile(filename);
        }
        deleteFileFromFileInfo(context, filename);
    }


    public static synchronized void deleteFileFromFileInfo(final Context context, String filename) {
        Map<String, List<String>> fileInfos = readFileInfos(context);
        Map<String, List<String>> fileInfostmp = new HashMap<> (fileInfos);
        for (Map.Entry<String, List<String>> entry : fileInfos.entrySet()) {
            if (entry.getKey().equals(filename)) {
                fileInfostmp.remove(entry.getKey());
                break;
            }
        }
        fileInfos = new HashMap<> (fileInfostmp);
        updateFileInfos(context, fileInfos);
    }

    public static synchronized void deleteFileInfo(final Context context) {
        if (context.getFileStreamPath(FILESINFO).exists()) {
            context.deleteFile(FILESINFO);
        }
    }

    /*protected void addTrajet(Position position){
        Log.i("*** PERSIST ADD", " ");
        try {
            FileOutputStream outputStream = context.openFileOutput(context.getFilesDir().getAbsolutePath()+ "/" + FILENAME, Context.MODE_APPEND);
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(outputStream));
            oos.writeObject(position);
            oos.flush();
            oos.close();
        } catch (IOException e) {
        }
*/
}
