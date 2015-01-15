public enum Movement {
	UP(0), DOWN(1), RIGHT(2), LEFT(3);

	private final int value;

	Movement(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}
}
