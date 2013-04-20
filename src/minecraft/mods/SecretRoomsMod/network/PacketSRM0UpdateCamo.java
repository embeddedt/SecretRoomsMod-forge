package mods.SecretRoomsMod.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import mods.SecretRoomsMod.blocks.BlockCamoDoor;
import mods.SecretRoomsMod.blocks.TileEntityFull;
import mods.SecretRoomsMod.common.BlockHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketSRM0UpdateCamo extends PacketSRMBase
{
	public final int			x, y, z;
	public final BlockHolder	holder;
	public final boolean		hasHolder;
	public final boolean[]		sides	= new boolean[6];

	public PacketSRM0UpdateCamo(TileEntityFull entity)
	{
		holder = entity.getBlockHolder();
		hasHolder = holder != null;
		x = entity.xCoord;
		y = entity.yCoord;
		z = entity.zCoord;

		for (int i = 0; i < 6; i++)
			sides[i] = entity.isCamo[i];
	}

	public PacketSRM0UpdateCamo(ObjectInputStream stream) throws IOException
	{
		x = stream.readInt();
		y = stream.readInt();
		z = stream.readInt();

		hasHolder = stream.readBoolean();

		if (hasHolder)
		{
			NBTTagCompound nbt = (NBTTagCompound) NBTBase.readNamedTag(stream);
			holder = BlockHolder.buildFromNBT(nbt);
		}
		else
			holder = null;

		for (int i = 0; i < 6; i++)
			sides[i] = stream.readBoolean();
	}

	@Override
	public void writeToStream(ObjectOutputStream stream) throws IOException
	{
		stream.writeInt(x);
		stream.writeInt(y);
		stream.writeInt(z);

		stream.writeBoolean(hasHolder);

		if (hasHolder)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			holder.writeToNBT(nbt);
			NBTBase.writeNamedTag(nbt, stream);
		}

		for (int i = 0; i < 6; i++)
			stream.writeBoolean(sides[i]);
	}

	@Override
	public int getID()
	{
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void actionClient(World world, EntityPlayer player)
	{
		if (world == null)
			return;

		TileEntityFull entity = (TileEntityFull) world.getBlockTileEntity(x, y, z);

		if (entity == null || holder == null)
			return;

		entity.setBlockHolder(holder);
		entity.isCamo = sides;

		if (entity.blockType instanceof BlockCamoDoor)
			world.markBlockForRenderUpdate(x, y + 1, z);

		world.markBlockForRenderUpdate(x, y, z);
	}

	@Override
	public void actionServer(World world, EntityPlayer player)
	{
		if (world == null)
			return;

		TileEntityFull entity = (TileEntityFull) world.getBlockTileEntity(x, y, z);

		if (entity == null || holder == null)
			return;

		entity.setBlockHolder(holder);
		entity.isCamo = sides;

		world.markBlockForRenderUpdate(x, y, z);
	}

}
