'use server';

import { db } from '@/lib/db/drizzle';
import { devices } from '@/lib/db/schema';
import { getTeamForUser } from '@/lib/db/queries';
import { revalidatePath } from 'next/cache';
import { eq, and } from 'drizzle-orm';

export async function createDevice(formData: FormData) {
  const team = await getTeamForUser();
  if (!team) {
    return { error: 'User is not part of a team.' };
  }

  const deviceId = formData.get('deviceId') as string;
  if (!deviceId || deviceId.trim() === '') {
    return { error: 'Device ID is required.' };
  }

  try {
    await db.insert(devices).values({
      id: deviceId.trim(),
      teamId: team.id,
    });
    revalidatePath('/dashboard/devices');
    return { success: 'Device created successfully.' };
  } catch (error) {
    return { error: 'Failed to create device. ID might already exist.' };
  }
}

export async function deleteDevice(formData: FormData) {
  const team = await getTeamForUser();
  if (!team) {
    return { error: 'User is not part of a team.' };
  }
  const deviceId = formData.get('deviceId') as string;

  try {
    await db.delete(devices).where(and(eq(devices.id, deviceId), eq(devices.teamId, team.id)));
    revalidatePath('/dashboard/devices');
    return { success: 'Device deleted successfully.' };
  } catch (error) {
    return { error: 'Failed to delete device.' };
  }
}
