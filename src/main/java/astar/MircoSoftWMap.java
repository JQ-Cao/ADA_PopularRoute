package astar;


import model.CaPath;

import java.util.*;


public class MircoSoftWMap {
	ArrayList<ArrayList<CaPath>> paths;
	
	HashMap<String,CaPath> linkHM;
	HashMap<String,ArrayList<String>> adjHM;	
	HashMap<String,Double> costHM;	
	
	ArrayList<String> startNodes;
	ArrayList<String> endNodes;	
	
	public MircoSoftWMap(ArrayList<ArrayList<CaPath>> paths) {
		this.paths = paths;
		init(paths);
	}

	private void init(ArrayList<ArrayList<CaPath>> paths){
		linkHM=new HashMap<String,CaPath>();
		adjHM=new HashMap<String,ArrayList<String>>();	
		costHM=new HashMap<String,Double>();
		
		startNodes=new ArrayList<String>();
		endNodes=new ArrayList<String>();
		int size=paths.size();
//		for(int i=size-1;i>=0;i--){			
//			ArrayList<CaPath> cPaths =paths.get(i);
//			if(i>0){
//				ArrayList<CaPath> ePaths =paths.get(i-1);
//				filter(ePaths,cPaths);
//			}
//		}
		//System.out.println("++++++++++++++++++++++++++++");
//		for(int i=0;i<size;i++){			
//			ArrayList<CaPath> cPaths =paths.get(i);
//			if(i<size-1){
//				ArrayList<CaPath> ePaths =paths.get(i+1);
//				if(ePaths.size()==0){
//					System.out.println("size=0");
//				}
//				filter(cPaths,ePaths);
//			}
//		}
		if(paths.size()>2){
			filterStartEnd(paths.get(0),paths.get(paths.size()-1));	
		}
		
		for(int i=0;i<size;i++){			
			ArrayList<CaPath> cPaths = paths.get(i);
//			if(i<(size-1)){
//				ArrayList<CaPath> ePaths =paths.get(i+1);
//				filter(cPaths,ePaths);
//			}
			int csize = cPaths.size();
//			if(csize==0){
//				System.out.println("cpath=0");
//			}
			//System.out.println("++++++++++++++++++++++++++++");
			for(int j=0;j<csize;j++){
				CaPath cpath=cPaths.get(j);
				
				String snodeid=i+"@"+cpath.getSt().linkID;
//				if(snodeid.equalsIgnoreCase("3@-14226907")){
//					System.out.println("");
//				}
				String enodeid=(i+1)+"@"+cpath.getEd().linkID;
				//System.out.println(cpath.getSt().getLinkID()+"->"+cpath.getEd().getLinkID()+":"+cpath.getPathsLength());
				//double cost=-(Math.log(cpath.getCost())+Math.log(cpath.getEd().cost));
				double pj = cpath.getSt().getPrjDistance()+cpath.getEd().getPrjDistance();
				double cost = (cpath.getCost()
						+pj
//				double cost = (cpath.getPathsLength()
//						+pj
//						+
//						(cpath.getSt().linkLenth-cpath.getSt().prjDistanceFormSNode)
//						+cpath.getEd().prjDistanceFormSNode
						);
				//System.out.println(cost);
				linkHM.put(snodeid+"_"+enodeid, cpath);
				costHM.put(snodeid+"_"+enodeid, new Double(cost));
				if(adjHM.containsKey(snodeid)){
					adjHM.get(snodeid).add(enodeid);
				}else{
					ArrayList<String> as=new ArrayList<String>();
					as.add(enodeid);
					adjHM.put(snodeid, as);
				}
				
				if(i==0){//处理第一个GPS点
					startNodes.add(snodeid);
					//double snodeCost=-Math.log(cpath.getSt().cost);
					//double snodeCost=cpath.getSt().getLinkLenth()-cpath.getSt().getPrjDistanceFormSNode();
					//double snodeCost= 0.0;
					//costHM.put(snodeid+"_"+snodeid, new Double(snodeCost));
				}
				
				if(i==size-1){//处理最后一个GPS点
					endNodes.add(enodeid);
				}
			}
		}
		//print("|",startNodes);
	}
	void print(String root,ArrayList<String> ajc){
		for(String c:ajc){
			ArrayList<String> a = adjHM.get(c);
			if(a ==null){
				System.out.println(root+"->"+c);
			}else{
				print(root+"->"+c,a);
			}
		}
	}
	
	void filter(ArrayList<CaPath> s , ArrayList<CaPath> e){
		HashMap<String,List<CaPath>> smap = new HashMap<String,List<CaPath>>();
		List<CaPath> slst = new ArrayList<CaPath>();
		for(int i=0;i<s.size();i++){
			CaPath p = s.get(i);
			String id = p.getEd().getLinkID();
			if(smap.containsKey(id)){
				//smap.get(id).add(p);
				///s.remove(p);
				slst.add(p);
			}else{
				List<CaPath> l = new ArrayList<CaPath>();
				l.add(p);
				smap.put(id, l);
			}
		}
		
		HashMap<String,List<CaPath>> emap = new HashMap<String,List<CaPath>>();
		List<CaPath> elst = new ArrayList<CaPath>();
		for(int i=0;i<e.size();i++){
			CaPath p = e.get(i);
			String id = p.getSt().getLinkID();
			if(emap.containsKey(id)){
				//emap.get(id).add(p);	
				elst.add(p);
			}else{
				List<CaPath> l = new ArrayList<CaPath>();
				l.add(p);
				emap.put(id, l);
			}
		}
		//同终点的保存权重最小的CaPath
		//System.out.println("-------------");
		//if(s.size()>5){
//			for(CaPath p: slst){
//				s.remove(p);
//			}			
		//}
//		if(e.size()>5){
//			for(CaPath p: elst){
//				e.remove(p);
//			}
//		}
		
		Set<String> sSet = new TreeSet<String>(smap.keySet());
		sSet.removeAll(emap.keySet());
		for(String key:sSet){
			List<CaPath> ps = smap.get(key);
			//System.out.println("remove s "+s.size()+"/"+ps.size());
			for(CaPath p:ps){
				s.remove(p);
			}
		}
		Set<String> eSet = new TreeSet<String>(emap.keySet());
		eSet.removeAll(smap.keySet());
						
		for(String key:eSet){
			List<CaPath> ps = emap.get(key);
			//System.out.println("remove e "+e.size()+"/"+ps.size());
			for(CaPath p:ps){
				e.remove(p);
			}
		}
	}
	
	private List<CaPath> sortBestCaPath(List<CaPath> caPathList/**,int top*/) {
		Collections.sort(caPathList, new Comparator<CaPath>() {
			public int compare(CaPath o1,
					CaPath o2) {
				//double[] o2v = o2.getValue();
				//double[] o1v = o1.getValue();
				double t1 = o1.getCost();
				double t2 = o2.getCost();
//				double t1 = o1.getPathsLength()+o1.getSt().getPrjDistance()+o1.getEd().getPrjDistance();
//				double t2 = o2.getPathsLength()+o2.getSt().getPrjDistance()+o2.getEd().getPrjDistance();
				return (t1 == t2 ? 0 : (t1 > t2 ? 1 : -1));
			}
		});
		//return caPathList.subList(0, top);
		return caPathList;
	}
	
	void filterStartEnd(ArrayList<CaPath> s , ArrayList<CaPath> e){
		sortBestCaPath(s);
		sortBestCaPath(e);
		HashMap<String,List<CaPath>> smap = new HashMap<String,List<CaPath>>();
		List<CaPath> slst = new ArrayList<CaPath>();
		for(int i=0;i<s.size();i++){
			CaPath p = s.get(i);
			String id = p.getEd().getLinkID();
			if(smap.containsKey(id)){
				//smap.get(id).add(p);
				///s.remove(p);
				slst.add(p);
			}else{
				List<CaPath> l = new ArrayList<CaPath>();
				l.add(p);
				smap.put(id, l);
			}
		}
		
		HashMap<String,List<CaPath>> emap = new HashMap<String,List<CaPath>>();
		List<CaPath> elst = new ArrayList<CaPath>();
		for(int i=0;i<e.size();i++){
			CaPath p = e.get(i);
			String id = p.getSt().getLinkID();
			if(emap.containsKey(id)){
				//emap.get(id).add(p);	
				elst.add(p);
			}else{
				List<CaPath> l = new ArrayList<CaPath>();
				l.add(p);
				emap.put(id, l);
			}
		}
		//同终点的保存权重最小的CaPath
		//System.out.println("-------------");
		//if(s.size()>5){
			for(CaPath p: slst){
				s.remove(p);
			}			
		//}
//		if(e.size()>5){
			for(CaPath p: elst){
				e.remove(p);
			}
//		}
		
	}
	
	public ArrayList<String> getAdjNodes(String nodeid){
		return adjHM.get(nodeid);
	}
	
	public Double getCost(String sid,String eid){
		return costHM.get(sid+"_"+eid);
	}
	
	public CaPath getCaPath(String sid,String eid){
		return linkHM.get(sid+"_"+eid);
	}
	
	public ArrayList<String> getStartNodes() {
		return startNodes;
	}

	public ArrayList<String> getEndNodes() {
		return endNodes;
	}
	
}
