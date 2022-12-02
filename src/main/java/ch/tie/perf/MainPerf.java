package ch.tie.perf;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class MainPerf {

  private MainPerf() {
  }

  public static final Option HELP_OPT = new Option("help", "print this message");

  public static final Option THREADS_OPT = Option.builder("t")
      .longOpt("threads")
      .hasArg()
      .type(Integer.class)
      .desc("number of test threads")
      .build();

  public static final Option ENDPOINT_OPT = Option.builder("u")
      .longOpt("url")
      .hasArg()
      .required()
      .desc("rest2 endpoint url")
      .build();


  public static final Option SAVE_FILE_OPT = Option.builder("s")
      .longOpt("savefile")
      .hasArg()
      .type(Boolean.class)
      .desc("store returned file")
      .build();

  public static final Option EXPERIMENT_NAME_OPT = Option.builder("e")
      .longOpt("experiment")
      .hasArg()
      .type(Boolean.class)
      .desc("name of experiment")
      .build();


  public static Options initMainOptions() {
    Options options = new Options();
    options.addOption(HELP_OPT);
    options.addOption(THREADS_OPT);
    options.addOption(ENDPOINT_OPT);
    options.addOption(SAVE_FILE_OPT);
    options.addOption(EXPERIMENT_NAME_OPT);

    return options;

  }

}
