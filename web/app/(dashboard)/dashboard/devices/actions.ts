'use server';

import { db } from '@/lib/db/drizzle';
import { devices } from '@/lib/db/schema';
import { getTeamForUser } from '@/lib/db/queries';
import { revalidatePath } from 'next/cache';
import { eq, and } from 'drizzle-orm';

export async function createDevice(formData: FormData) {
  const team = await getTeamForUser();
  if (!team) return;

  const deviceId = formData.get('deviceId') as string;
  if (!deviceId || deviceId.trim() === '') return;

  try {
    await db.insert(devices).values({
      id: deviceId.trim(),
      teamId: team.id,
    });
    revalidatePath('/dashboard/devices');
  } catch (error) {
    // Handle error
  }
}

export async function deleteDevice(formData: FormData) {
  const team = await getTeamForUser();
  if (!team) return;
  const deviceId = formData.get('deviceId') as string;

  try {
    await db.delete(devices).where(and(eq(devices.id, deviceId), eq(devices.teamId, team.id)));
    revalidatePath('/dashboard/devices');
  } catch (error) {
    // Handle error
  }
}

