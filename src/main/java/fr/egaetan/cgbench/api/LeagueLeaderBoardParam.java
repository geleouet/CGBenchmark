package fr.egaetan.cgbench.api;

import java.util.ArrayList;

public class LeagueLeaderBoardParam extends ArrayList<Object> {

	static class DivisionParam {
		int divisionId;
		int roomIndex;
		
		public DivisionParam(int divisionId, int roomIndex) {
			super();
			this.divisionId = divisionId;
			this.roomIndex = roomIndex;
		}

		public int getDivisionId() {
			return divisionId;
		}

		public void setDivisionId(int divisionId) {
			this.divisionId = divisionId;
		}

		public int getRoomIndex() {
			return roomIndex;
		}

		public void setRoomIndex(int roomIndex) {
			this.roomIndex = roomIndex;
		}
		
		
	}
	
	public LeagueLeaderBoardParam(int divisionId) {
		add(new DivisionParam(divisionId, 0));
		add(null);
		add(null);
		add(null);
	}
	
}
