package stopwatch;

/**
 * @author vendetta
 *
 */
public class Timer implements ITimer {
	private long startTime;
	private long duration;

	@Override
	public void start() {
        this.duration = 0;
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public long stop() {
		duration += System.currentTimeMillis()-this.startTime;
		return duration;
	}

	@Override
	public void resume() {
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public long pause() {
        duration += System.currentTimeMillis()-this.startTime;
        return duration;
	}
}
