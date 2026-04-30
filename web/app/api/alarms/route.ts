import { NextRequest, NextResponse } from 'next/server';
import { db } from '@/lib/db/drizzle';
import { alarms } from '@/lib/db/schema';
import { eq } from 'drizzle-orm';

export async function GET(req: NextRequest) {
  const searchParams = req.nextUrl.searchParams;
  const deviceId = searchParams.get('deviceId');

  if (!deviceId) {
    return NextResponse.json({ error: 'deviceId is required' }, { status: 400 });
  }

  try {
    const deviceAlarms = await db.query.alarms.findMany({
      where: eq(alarms.deviceId, deviceId),
      orderBy: (alarms, { asc }) => [asc(alarms.time)],
    });

    return NextResponse.json(deviceAlarms);
  } catch (error) {
    return NextResponse.json({ error: 'Failed to fetch alarms' }, { status: 500 });
  }
}
