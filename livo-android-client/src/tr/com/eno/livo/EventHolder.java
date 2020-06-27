package tr.com.eno.livo;

import java.io.Serializable;




import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import tr.com.eno.livo.thrift.analytics.Event;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class EventHolder implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private SharedPreferences.Editor editor;
	
	private Context context;
	
	public EventHolder(Context context){
		
		this.context = context;
		
		this.editor = this.context.getSharedPreferences("EventCache", Context.MODE_PRIVATE).edit();
		
	}
	
	public synchronized void addEventsToCache(List<Event> events){
		
		Gson gson = new Gson();
		
	    this.editor.putString("Events",gson.toJson(events));
	    
	    this.editor.commit();
		
		
	}
	
	public synchronized List<Event> getCachedEvents(){
		
		Gson gson = new Gson();
		
		Type listType = new TypeToken<LinkedList<Event>>() {
        }.getType();
		String s = this.context.getSharedPreferences("EventCache", Context.MODE_PRIVATE).getString("Events", null);
		
		
		List<Event> list = gson.fromJson(s, listType);
		
		if(list== null){
			list = new LinkedList<Event>();
		}
		
		return list;
				
	}
	
//	public boolean isThereAny(){
//		boolean isThere;
//		try{
//
//			isThere = this.getCachedEvents().size()>0;
//
//			Log.d("EventHolder", "Cached events size : "+this.getCachedEvents().size());
//
//		}catch(Exception e){
//
//			Log.d("EventHolder", "No saved events found.");
//
//			isThere = false;
//		}
//		return isThere;
//	}
	

}
