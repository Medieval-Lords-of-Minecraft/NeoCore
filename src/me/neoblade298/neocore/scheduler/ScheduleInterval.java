package me.neoblade298.neocore.scheduler;

public enum ScheduleInterval {
	DAILY(-1),
	HOUR(60),
	HALF_HOUR(30),
	FIFTEEN_MINUTES(15);
	private final int divisor;
	private ScheduleInterval(final int divisor) {
		this.divisor = divisor;
	}
	
	public int getDivisor() {
		return divisor;
	}
}
