import { db } from '@/lib/db/drizzle';
import { devices } from '@/lib/db/schema';
import { getTeamForUser } from '@/lib/db/queries';
import { eq } from 'drizzle-orm';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { createDevice, deleteDevice } from './actions';

export default async function DevicesPage() {
  const team = await getTeamForUser();
  if (!team) {
    return <div>Please join a team first.</div>;
  }

  const teamDevices = await db.query.devices.findMany({
    where: eq(devices.teamId, team.id),
    orderBy: (devices, { desc }) => [desc(devices.createdAt)],
  });

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-medium">Devices</h1>
      
      <Card>
        <CardHeader>
          <CardTitle>Register New Device</CardTitle>
        </CardHeader>
        <CardContent>
          <form action={createDevice} className="flex gap-4 items-end">
            <div className="flex-1">
              <Label htmlFor="deviceId">Alphanumeric Device ID</Label>
              <Input id="deviceId" name="deviceId" required placeholder="e.g. A1B2C3D4" />
            </div>
            <Button type="submit">Add Device</Button>
          </form>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Existing Devices</CardTitle>
        </CardHeader>
        <CardContent>
          {teamDevices.length === 0 ? (
            <p className="text-muted-foreground">No devices found.</p>
          ) : (
            <ul className="space-y-4">
              {teamDevices.map((device) => (
                <li key={device.id} className="flex justify-between items-center border-b pb-2">
                  <div className="font-medium">{device.id}</div>
                  <form action={deleteDevice}>
                    <input type="hidden" name="deviceId" value={device.id} />
                    <Button variant="destructive" size="sm" type="submit">Remove</Button>
                  </form>
                </li>
              ))}
            </ul>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
