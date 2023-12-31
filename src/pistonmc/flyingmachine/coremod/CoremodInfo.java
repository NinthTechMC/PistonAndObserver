package pistonmc.flyingmachine.coremod;

public interface CoremodInfo {
    String Id = "@modid@";
    String Version = "@version@";
    String Group = "@group@";
    String GroupInternal = "@groupInternal@";
    String CoremodGroup = Group + ".coremod";
    String CoremodGroupInternal = GroupInternal + "/coremod";
}

