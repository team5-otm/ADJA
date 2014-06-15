package at.vcity.androidim.interfaces;
import at.vcity.androidim.types.CompanyInfo;
import at.vcity.androidim.types.FriendInfo;
import at.vcity.androidim.types.MessageInfo;


public interface IUpdateData {
//	public void updateData(MessageInfo[] messages, FriendInfo[] friends, FriendInfo[] unApprovedFriends, String userKey);
	public void updateData(MessageInfo[] messages, CompanyInfo[] companys,String userKey);

}
