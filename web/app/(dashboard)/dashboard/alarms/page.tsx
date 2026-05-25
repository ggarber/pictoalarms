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
import { DeviceSelector } from './device-selector';
import { Plus, Save, Trash2 } from 'lucide-react';

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
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <h1 className="text-2xl font-medium">Alarms</h1>
        <DeviceSelector devices={teamDevices} selectedDeviceId={selectedDeviceId} />
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Add New Alarm for {selectedDeviceId}</CardTitle>
        </CardHeader>
        <CardContent>
          <form action={createAlarm} className="flex gap-6 items-center flex-wrap sm:flex-nowrap w-full">
            <input type="hidden" name="deviceId" value={selectedDeviceId} />
            <div className="flex items-center gap-2 flex-grow min-w-[120px]">
              <Label htmlFor="time" className="text-sm font-medium whitespace-nowrap">Time</Label>
              <Input id="time" name="time" type="time" className="w-full" required />
            </div>
            <div className="flex items-center gap-2 flex-grow min-w-[150px]">
              <Label htmlFor="pictogram" className="text-sm font-medium whitespace-nowrap">Pictogram</Label>
              <select 
                id="pictogram" 
                name="pictogram" 
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer"
              >
                <option value="BREAKFAST">Breakfast</option>
                <option value="BALL">Ball</option>
                <option value="PILL">Pill</option>
              </select>
            </div>
            <Button type="submit" size="icon" title="Add Alarm" className="shrink-0">
              <Plus className="h-4 w-4" />
              <span className="sr-only">Add Alarm</span>
            </Button>
          </form>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Current Alarms</CardTitle>
        </CardHeader>
        <CardContent>
          {deviceAlarms.length === 0 ? (
            <p className="text-muted-foreground">No alarms for this device.</p>
          ) : (
            <div className="space-y-4">
              {deviceAlarms.map((alarm) => (
                <div key={alarm.id}>
                  <form className="flex gap-6 items-center flex-wrap sm:flex-nowrap w-full">
                    <input type="hidden" name="alarmId" value={alarm.id} />
                    <div className="flex items-center gap-2 flex-grow min-w-[120px]">
                      <Label htmlFor={`time-${alarm.id}`} className="text-sm font-medium whitespace-nowrap">Time</Label>
                      <Input id={`time-${alarm.id}`} name="time" type="time" defaultValue={alarm.time} className="w-full" required />
                    </div>
                    <div className="flex items-center gap-2 flex-grow min-w-[150px]">
                      <Label htmlFor={`picto-${alarm.id}`} className="text-sm font-medium whitespace-nowrap">Pictogram</Label>
                      <select 
                        id={`picto-${alarm.id}`}
                        name="pictogram" 
                        defaultValue={alarm.pictogram}
                        className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer"
                      >
                        <option value="BREAKFAST">Breakfast</option>
                        <option value="BALL">Ball</option>
                        <option value="PILL">Pill</option>
                      </select>
                    </div>
                    <div className="flex gap-2 shrink-0">
                      <Button type="submit" formAction={updateAlarm} variant="secondary" size="icon" title="Update Alarm">
                        <Save className="h-4 w-4" />
                        <span className="sr-only">Update</span>
                      </Button>
                      <Button type="submit" formAction={deleteAlarm} variant="destructive" size="icon" title="Remove Alarm">
                        <Trash2 className="h-4 w-4" />
                        <span className="sr-only">Remove</span>
                      </Button>
                    </div>
                  </form>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
