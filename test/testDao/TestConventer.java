package testDao;

import org.json.JSONObject;

import com.seahold.dao.wrapper.MultiFieldConventer;

public class TestConventer implements MultiFieldConventer<Integer>{

	@Override
	public Integer multiFieldConvent(JSONObject json) {
		if(json.has("id")){
			return json.getInt("id")+10;
		}else{
			return 0;
		}
	}

}
