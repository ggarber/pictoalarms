import { db } from '@/lib/db/drizzle';
import { alarms, devices } from '@/lib/db/schema';
import { getTeamForUser } from '@/lib/db/queries';
import { eq } from 'drizzle-orm';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { createAlarm, deleteAlarm, updateAlarm } from './actions';
import { redirect } from 'next/navigation';

export default async function AlarmsPage({ searchParams }: { searchParams: Promise<{ deviceId?: string }> }) {
  const params = await searchParams;
  const team = await getTeamForUser();
  if (!team) {
    return <div>Please join a team first.</div>;
  }

  const teamDevices = await db.query.devices.findMany({
    where: eq(devices.teamId, team.id),
    orderBy: (devices, { desc }) => [desc(devices.createdAt)],
  });

  if (teamDevices.length === 0) {
    return <div className="p-8">No devices found. Please create a device first.</div>;
  }

  const selectedDeviceId = params.deviceId || teamDevices[0].id;
  const selectedDevice = teamDevices.find(d => d.id === selectedDeviceId);

  if (!selectedDevice && params.deviceId) {
    redirect('/dashboard/alarms');
  }

  const deviceAlarms = await db.query.alarms.findMany({
    where: eq(alarms.deviceId, selectedDeviceId),
    orderBy: (alarms, { asc }) => [asc(alarms.time)],
  });

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-medium">Alarms</h1>
      
      <Card>
        <CardHeader>
          <CardTitle>Select Device</CardTitle>
        </CardHeader>
        <CardContent>
          <form method="GET" action="/dashboard/alarms" className="flex gap-4 items-end">
            <div className="flex-1">
              <Label htmlFor="deviceId">Device</Label>
              <select 
                id="deviceId" 
                name="deviceId" 
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                defaultValue={selectedDeviceId}
              >
                {teamDevices.map(d => (
                  <option key={d.id} value={d.id}>{d.id}</option>
                ))}
              </select>
            </div>
            <Button type="submit" variant="secondary">View Alarms</Button>
          </form>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Add New Alarm for {selectedDeviceId}</CardTitle>
        </CardHeader>
        <CardContent>
          <form action={createAlarm} className="flex gap-4 items-end flex-wrap">
            <input type="hidden" name="deviceId" value={selectedDeviceId} />
            <div className="flex-1 min-w-[150px]">
              <Label htmlFor="time">Time</Label>
              <Input id="time" name="time" type="time" required />
            </div>
            <div className="flex-1 min-w-[150px]">
              <Label htmlFor="pictogram">Pictogram</Label>
              <select 
                id="pictogram" 
                name="pictogram" 
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
              >
                <option value="BREAKFAST">Breakfast</option>
                <option value="BALL">Ball</option>
                <option value="PILL">Pill</option>
              </select>
            </div>
            <Button type="submit">Add Alarm</Button>
          </form>
        </CardContent>
      </Card>

      <div className="space-y-4">
        <h2 className="text-xl font-medium">Current Alarms</h2>
        {deviceAlarms.length === 0 ? (
          <p className="text-muted-foreground">No alarms for this device.</p>
        ) : (
          <div className="grid gap-4">
            {deviceAlarms.map((alarm) => (
              <Card key={alarm.id}>
                <CardContent className="p-4">
                  <form className="flex gap-4 items-end flex-wrap">
                    <input type="hidden" name="alarmId" value={alarm.id} />
                    <div className="flex-1 min-w-[150px]">
                      <Label htmlFor={`time-${alarm.id}`}>Time</Label>
                      <Input id={`time-${alarm.id}`} name="time" type="time" defaultValue={alarm.time} required />
                    </div>
                    <div className="flex-1 min-w-[150px]">
                      <Label htmlFor={`picto-${alarm.id}`}>Pictogram</Label>
                      <select 
                        id={`picto-${alarm.id}`}
                        name="pictogram" 
                        defaultValue={alarm.pictogram}
                        className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                      >
                        <option value="BREAKFAST">Breakfast</option>
                        <option value="BALL">Ball</option>
                        <option value="PILL">Pill</option>
                      </select>
                    </div>
                    <div className="flex gap-2">
                      <Button type="submit" formAction={updateAlarm} variant="secondary">Update</Button>
                      <Button type="submit" formAction={deleteAlarm} variant="destructive">Remove</Button>
                    </div>
                  </form>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
