package foodsimulationmodel.relogo.environment;

import java.io.File;

import repast.simphony.batch.BatchScenarioLoader;
import repast.simphony.engine.controller.Controller;
import repast.simphony.engine.controller.DefaultController;
import repast.simphony.engine.environment.AbstractRunner;
import repast.simphony.engine.environment.ControllerRegistry;
import repast.simphony.engine.environment.DefaultRunEnvironmentBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunEnvironmentBuilder;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.BoundParameters;
import repast.simphony.parameter.DefaultParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.SweeperProducer;
import simphony.util.messages.MessageCenter;

public class TestRunner_2 extends AbstractRunner {

	private static MessageCenter msgCenter = MessageCenter.getMessageCenter(TestRunner_2.class);

	private RunEnvironmentBuilder runEnvironmentBuilder;
	protected Controller controller;
	protected boolean pause = false;
	protected Object monitor = new Object();
	protected SweeperProducer producer;
	private ISchedule schedule;
	protected Parameters parameters;

	public TestRunner_2() {
		runEnvironmentBuilder = new DefaultRunEnvironmentBuilder(this, true);
		controller = new DefaultController(runEnvironmentBuilder);
		controller.setScheduleRunner(this);
		parameters = new BoundParameters(new DefaultParameters());
	}

	public void load(File scenarioDir) throws Exception{
		if (scenarioDir.exists()) {
			BatchScenarioLoader loader = new BatchScenarioLoader(scenarioDir);
			ControllerRegistry registry = loader.load(runEnvironmentBuilder);
			controller.setControllerRegistry(registry);
		} else {
			msgCenter.error("Scenario not found", new IllegalArgumentException(
					"Invalid scenario " + scenarioDir.getAbsolutePath()));
			return;
		}

		controller.batchInitialize();
		controller.runParameterSetters(null);
	}


	
	public void runInitialize(){
	((DefaultParameters) parameters).addParameter("randomSeed", "Default Random Seed", int.class, 914509816, false);
	((DefaultParameters) parameters).addParameter("default_observer_maxPxcor", "Max X Coordinate", int.class, 16, false);
	((DefaultParameters) parameters).addParameter("default_observer_maxPycor", "Max Y Coordinate", int.class, 16, false);
	((DefaultParameters) parameters).addParameter("default_observer_minPxcor", "Min X Coordinate", int.class, -16, false);
	((DefaultParameters) parameters).addParameter("default_observer_minPycor", "Min Y Coordinate", int.class, -16, false);
	RunEnvironment.getInstance().setParameters(parameters);
	Parameters param2 = RunEnvironment.getInstance().getParameters();
	System.out.println(param2.getValue("default_observer_minPxcor"));
    controller.runInitialize(parameters);
		schedule = RunState.getInstance().getScheduleRegistry().getModelSchedule();
	}

	public void cleanUpRun(){
		controller.runCleanup();
	}
	public void cleanUpBatch(){
		controller.batchCleanup();
	}

	// returns the tick count of the next scheduled item
	public double getNextScheduledTime(){
		return ((Schedule)RunEnvironment.getInstance().getCurrentSchedule()).peekNextAction().getNextTime();
	}

	// returns the number of model actions on the schedule
	public int getModelActionCount(){
		return schedule.getModelActionCount();
	}

	// returns the number of non-model actions on the schedule
	public int getActionCount(){
		return schedule.getActionCount();
	}

	// Step the schedule
	public void step(){
    schedule.execute();
	}

	// stop the schedule
	public void stop(){
		if ( schedule != null )
			schedule.executeEndActions();
	}

	public void setFinishing(boolean fin){
		schedule.setFinishing(fin);
	}

	public void execute(RunState toExecuteOn) {
		// required AbstractRunner stub.  We will control the
		//  schedule directly.
	}
}
