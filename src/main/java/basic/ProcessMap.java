package basic;

import model.Link;
import model.Vertex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by caojiaqing on 26/04/2017.
 */
public class ProcessMap {
    //map<cellID,Set<Link>>
    public static Map<String,Set<Link>> process(String fileName) {
        Map<String, Set<Link>> cellLinkMap = new HashMap<>();
        Map<Long, Link> linkHashMap = new HashMap<>();
        Map<Long, Vertex> vertexHashMap = new HashMap<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] arr = line.substring(1,line.length()-1).split(",");
                double startLng = Double.parseDouble(arr[3]);
                double startLat = Double.parseDouble(arr[4]);
                long startID = Long.parseLong(arr[2]);
                String startCellID = MapUtil.findCell(startLng,startLat);


                double endLng = Double.parseDouble(arr[6]);
                double endLat = Double.parseDouble(arr[7]);
                long endID = Long.parseLong(arr[5]);
                String endCellID = MapUtil.findCell(endLng,endLat);

                vertexHashMap.putIfAbsent(startID,new Vertex(startLng,startLat,startID));
                vertexHashMap.putIfAbsent(endID,new Vertex(endLng,endLat,endID));

                long linkID = Long.parseLong(arr[0]);

                linkHashMap.putIfAbsent(linkID,new Link(linkID,vertexHashMap.get(startID),vertexHashMap.get(endID)));

                if(startCellID.equals(endCellID)){
                    Set<Link> linkSet = cellLinkMap.getOrDefault(startCellID,new HashSet<>());
                    linkSet.add(linkHashMap.get(linkID));
                    cellLinkMap.putIfAbsent(startCellID,linkSet);
                }else{
                    Set<Link> linkIdSet = cellLinkMap.getOrDefault(startCellID,new HashSet<>());
                    linkIdSet.add(linkHashMap.get(linkID));
                    cellLinkMap.putIfAbsent(startCellID,linkIdSet);

                    linkIdSet = cellLinkMap.getOrDefault(endCellID,new HashSet<>());
                    linkIdSet.add(linkHashMap.get(linkID));
                    cellLinkMap.putIfAbsent(endCellID,linkIdSet);

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return cellLinkMap;
    }

    public static void main(String[] arg){
        ProcessMap.process("/Users/caojiaqing/Repository/map/guangdong/zhuhai.txt");
    }
}
