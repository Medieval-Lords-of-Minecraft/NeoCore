package me.neoblade298.neocore.shared.commands;

public class HiddenSubcommand extends AbstractSubcommand {
	private String alias;
	private AbstractSubcommand cmd;
	public HiddenSubcommand(AbstractSubcommand cmd, String alias) {
		super(cmd.key, cmd.desc, cmd.perm, cmd.runner);
		this.hidden = true;
		this.alias = alias;
		this.cmd = cmd;
	}
	public String getAlias() {
		return alias;
	}
	public AbstractSubcommand getCmd() {
		return cmd;
	}
}
