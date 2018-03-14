package band; 

import javafx.application.Application;
import javafx.scene.*;
import javafx.event.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import java.util.Random;

public class Mars extends Application implements MicrophoneListener
{
	private final Microphone microphone;
	private final SoundProcessor soundProcessor;
	private Stage stage;
	private final Color[] colors;

	public Mars()
	{
		microphone = new Microphone(this);
		soundProcessor = new SoundProcessor();
		colors = new Color[7];
		colors[0] = Color.DARKBLUE;
		colors[1] = Color.DARKVIOLET;
		colors[2] = Color.INDIGO;
		colors[3] = Color.GREEN;
		colors[4] = Color.ORANGE;
		colors[5] = Color.LIGHTPINK;
		colors[6] = Color.YELLOW;

	}

	public void doLaunch(String[] args)
	{
		launch(args);
	}

	public void start (Stage stage)
	{
		this.stage = stage;


		final Circle[] circles = new Circle[7];
		for (int i = 0; i < circles.length; i++)
		{
		  circles[i] = new Circle(100*i+100, 500, 50, Color.LIGHTBLUE);
		}
		final Canvas canvas = new Canvas(1000, 1000);
		final Group group = new Group(canvas, circles[0], circles[1], circles[2], circles[3], circles[4], circles[5],circles[6]);
		


		final Scene scene = new Scene(group,1000,1000,Color.BLACK);


		final Random rand = new Random();

		stage.addEventHandler(MusicEvent.MUSIC_EVENT_TYPE, new EventHandler<MusicEvent>() {
		  @Override
		  public void handle(MusicEvent event)
		  {
		    double[] octaves =  event.getOctavePeaks();
		    for (int i=0; i<circles.length; i++)
		    {
		      circles[i].setRadius(20.0+octaves[i]);
		    }

		    GraphicsContext gc = canvas.getGraphicsContext2D();

		    for (int i=0; i<octaves.length;i++)
		    {
		      gc.setFill(colors[i]);
		      for (int j=5; j<(int)octaves[i]; j++)
		      {
		        gc.fillOval(rand.nextDouble()*1000.0, rand.nextDouble()*1000.0, 30, 30);
		      }
		    }
		  }
		});







		stage.setScene(scene);
		stage.show();

		microphone.listen();
	}

	public void onSoundHeard(float[] left, float[] right)
	{
		MusicEvent musicEvent = soundProcessor.processSound(left,right);
		stage.fireEvent(musicEvent);
	}

}