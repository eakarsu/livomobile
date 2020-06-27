package tr.com.eno.livo;


import java.util.HashSet;

import tr.com.eno.livo.thrift.shared.File;
import tr.com.eno.livo.thrift.shared.Profile;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

public class ProfileContainer
{

	
	private SharedPreferences.Editor profileContainer;
	
	private Context context;
  
	public ProfileContainer(Context context){
	  
	 this.context = context;
	  
	 this.profileContainer = this.context.getSharedPreferences("LivoProfiles", Context.MODE_PRIVATE).edit();
	 	 
  }
  
  public  Profile getServerProfile()
  {
		 
			Gson gson = new Gson(); 
			 
			String object = this.context.getSharedPreferences("LivoProfiles", Context.MODE_PRIVATE)
					.getString("serverprofile", null);
			
			return gson.fromJson(object,Profile.class) ; 

	  
  }
  
  public void setServerProfile(Profile arg){
	  
	  	Gson gson = new Gson();
		
	    this.profileContainer.putString("serverprofile",gson.toJson(arg) );
	    
	    this.profileContainer.commit();
  }
  

  
  /**
   * Returns current client profile
   * @return profile
   */
  public Profile getClientProfile(){
		  
	  if(this.context.getSharedPreferences("LivoProfiles", Context.MODE_PRIVATE).getString("clientprofile", null)==null)
	  {  
		  	Log.d("Profile Container", "Client Profile is null.");
			return  new Profile("1111111", new HashSet<File>());
	  }
		 else{

			Gson gson = new Gson(); 
			 
			String object = this.context.getSharedPreferences("LivoProfiles", Context.MODE_PRIVATE)
					.getString("clientprofile", null);
		    Log.d("Profile Container","Client Profile is not null ---> " + object);

			return gson.fromJson(object,Profile.class) ; 

		 }
  
  }
  /**
   * Saves current application's server profile, profile becomes client profile.
   * @param paramProfile server profile to save
   */
  public  void equalizeProfiles(Profile paramProfile)
  {
	
	Log.d("Profile Container","Equalizing profiles.");
	Gson gson = new Gson();
	
    this.profileContainer.putString("clientprofile",gson.toJson(paramProfile) );
    
    this.profileContainer.commit();
    
    ConfigPool pool = ConfigPool.getInstance(this.context);
    
    pool.setAppExistence(true);
    
    Log.d("Profile Container", "Profiles are equal now.");

    
	  
  }
 
}
