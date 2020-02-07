package oblig2;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Random;

public class MemberArchive {

	public static final int SILVER_LIMIT = 25000;
	public static final int GOLD_LIMIT = 75000;
	
	private HashMap<Integer, BonusMember> membersMap = new HashMap<Integer, BonusMember>();
	private Random randomGenerator = new Random();
	
	public int findPoints (int memberNo, String password) {
		BonusMember member = membersMap.get(memberNo);
		
		// Perform validation
		if (member == null)
			return -1;
		
		return member.okPassword(password) ? member.getBonuspoints() : -1;
	}
	
	public boolean registerPoints (int memberNo, int bonusPoints) {
		BonusMember member = membersMap.get(memberNo);
		
		// Perform validation
		if (member == null)
			return false;
		
		member.registerPoints(bonusPoints);
		return true;
	}
	
	public int addMember (Personals pers, LocalDate dateEnrolled) {
		BasicMember member = new BasicMember(findAvailableNo(), pers, dateEnrolled);
		membersMap.put(member.getMemberNo(), member);
		
		return member.getMemberNo();
	}
	
	private int findAvailableNo() {
		return randomGenerator
				.ints(0, 1000000)
				.filter(i -> membersMap.containsKey(i))
				.findAny()
				.getAsInt();
	}
	
	private BonusMember tryUpgradingMember (BonusMember member, LocalDate localDate) {
		int points = member.findQualificationPoints(localDate);
		
		if (points >= GOLD_LIMIT && !(member instanceof GoldMember))
			return new GoldMember(member.getMemberNo(), member.getPersonals(), member.getEnrolledDate(), member.getBonuspoints());
		if (points >= SILVER_LIMIT && !(member instanceof SilverMember))
			return new SilverMember(member.getMemberNo(), member.getPersonals(), member.getEnrolledDate(), member.getBonuspoints());
		
		return member;
	}
	
	public void checkMembers (LocalDate localDate) {
		membersMap.values().forEach(m -> membersMap.put(m.getMemberNo(), tryUpgradingMember(m, localDate)));
	}
	
}