package sailpoint.iiq.object;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Resources {
	
	int count;
	int startIndex;
	int totalResults;
	
	@SerializedName("Resources")
	List<User> resources;
	//List<? extends Resource> resources;
	
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}
	public List<? extends Resource> getResources() {
		return resources;
	}
	@SuppressWarnings("unchecked")
	public void setResources(List<? extends Resource> resources) {
		this.resources = (List<User>) resources;
	}
	public boolean isEmpty(){
		return resources.isEmpty();
	}
}
