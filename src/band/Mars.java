package band; 

public class Mars implements MicrophoneListener
{
	private final Microphone microphone ;

	public Mars()
	{
		microphone = new Microphone(this);
	}

	public void start ()
	{
		microphone.listen();
	}

	public void onSoundHeard(float[] left, float[] right)
	{
		System.out.println(String.format("%.2f",left[0]));
	}




}