import akka.actor.testkit.typed.CapturedLogEvent;
import akka.actor.testkit.typed.javadsl.BehaviorTestKit;
import akka.actor.testkit.typed.javadsl.TestInbox;
import com.click.models.ControlActor;
import com.click.models.DisplayActor;
import com.click.models.LocationActor;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RunningTests {
    @Test
    void PassesIfDisplayActorIsRunning (){
        BehaviorTestKit<DisplayActor.Command> testDisplayActor = BehaviorTestKit.create(DisplayActor.create());

        TestInbox<LocationActor.Command> testInbox = TestInbox.create();

        DisplayActor.Command message = new DisplayActor.NewPriceCommand(testInbox.getRef(),"TestProductName", 33333,33333, "TestLocation");
        testDisplayActor.run(message);
        List<CapturedLogEvent> logMessages = testDisplayActor.getAllLogEntries();
        assertEquals(logMessages.size(), 1);
        assertEquals(logMessages.get(0).message() , "Display actor running" );
        assertEquals(logMessages.get(0).level() , Level.DEBUG );
    }
    @Test
    void FailsIfDisplayActorIsNotResponding (){
        BehaviorTestKit<DisplayActor.Command> testDisplayActor = BehaviorTestKit.create(DisplayActor.create());

        TestInbox<LocationActor.Command> testInbox = TestInbox.create();

        DisplayActor.Command message = new DisplayActor.NewPriceCommand(testInbox.getRef(),"TestProductName", 33333,33333, "TestLocation");
        testDisplayActor.run(message);
        assertTrue(testInbox.hasMessages());
    }

}
