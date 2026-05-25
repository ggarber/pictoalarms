import { db } from '@/lib/db/drizzle';
import { devices } from '@/lib/db/schema';
import { getTeamForUser } from '@/lib/db/queries';
import { eq } from 'drizzle-orm';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { createDevice, deleteDevice } from './actions';
import { Plus, Trash2 } from 'lucide-react';

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
          <form action={createDevice} className="flex gap-6 items-center flex-wrap">
            <div className="flex items-center gap-2">
              <Label htmlFor="deviceId" className="text-sm font-medium whitespace-nowrap">Alphanumeric Device ID</Label>
              <Input id="deviceId" name="deviceId" required placeholder="e.g. A1B2C3D4" className="w-[200px]" />
            </div>
            <Button type="submit" size="icon" title="Add Device">
              <Plus className="h-4 w-4" />
              <span className="sr-only">Add Device</span>
            </Button>
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
                <li key={device.id} className="flex justify-between items-center">
                  <div className="font-medium">{device.id}</div>
                  <form action={deleteDevice}>
                    <input type="hidden" name="deviceId" value={device.id} />
                    <Button variant="destructive" size="icon" type="submit" title="Remove Device">
                      <Trash2 className="h-4 w-4" />
                      <span className="sr-only">Remove</span>
                    </Button>
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
