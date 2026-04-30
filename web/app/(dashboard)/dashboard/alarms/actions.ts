'use server';

import { db } from '@/lib/db/drizzle';
import { alarms, devices } from '@/lib/db/schema';
import { getTeamForUser } from '@/lib/db/queries';
import { revalidatePath } from 'next/cache';
import { eq, and } from 'drizzle-orm';

export async function createAlarm(formData: FormData) {
  const team = await getTeamForUser();
  if (!team) return { error: 'Not authenticated' };

  const deviceId = formData.get('deviceId') as string;
  const time = formData.get('time') as string;
  const pictogram = formData.get('pictogram') as string;

  const device = await db.query.devices.findFirst({
    where: and(eq(devices.id, deviceId), eq(devices.teamId, team.id))
  });
  if (!device) return { error: 'Device not found or not owned by team' };

  try {
    await db.insert(alarms).values({
      deviceId,
      time,
      pictogram
    });
    revalidatePath(`/dashboard/alarms`);
    return { success: 'Alarm created' };
  } catch(e) {
    return { error: 'Failed to create alarm' };
  }
}

export async function deleteAlarm(formData: FormData) {
  const team = await getTeamForUser();
  if (!team) return { error: 'Not authenticated' };
  
  const alarmIdStr = formData.get('alarmId') as string;
  const alarmId = parseInt(alarmIdStr, 10);
  
  const alarm = await db.query.alarms.findFirst({
    where: eq(alarms.id, alarmId),
    with: { device: true }
  });

  if (!alarm || alarm.device.teamId !== team.id) {
    return { error: 'Not allowed' };
  }

  try {
    await db.delete(alarms).where(eq(alarms.id, alarmId));
    revalidatePath('/dashboard/alarms');
    return { success: 'Alarm deleted' };
  } catch(e) {
    return { error: 'Failed to delete alarm' };
  }
}

export async function updateAlarm(formData: FormData) {
  const team = await getTeamForUser();
  if (!team) return { error: 'Not authenticated' };

  const alarmId = parseInt(formData.get('alarmId') as string, 10);
  const time = formData.get('time') as string;
  const pictogram = formData.get('pictogram') as string;

  const alarm = await db.query.alarms.findFirst({
    where: eq(alarms.id, alarmId),
    with: { device: true }
  });

  if (!alarm || alarm.device.teamId !== team.id) {
    return { error: 'Not allowed' };
  }

  try {
    await db.update(alarms).set({ time, pictogram }).where(eq(alarms.id, alarmId));
    revalidatePath('/dashboard/alarms');
    return { success: 'Alarm updated' };
  } catch(e) {
    return { error: 'Failed to update alarm' };
  }
}
