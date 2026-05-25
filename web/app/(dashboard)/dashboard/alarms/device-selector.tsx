'use client';

import { useRouter } from 'next/navigation';
import { Label } from '@/components/ui/label';

export function DeviceSelector({
  devices,
  selectedDeviceId
}: {
  devices: { id: string }[];
  selectedDeviceId: string;
}) {
  const router = useRouter();

  return (
    <div className="flex items-center gap-2">
      <Label htmlFor="deviceId" className="text-sm font-medium text-muted-foreground whitespace-nowrap">
        Device
      </Label>
      <select
        id="deviceId"
        name="deviceId"
        className="flex h-10 w-[240px] rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer"
        value={selectedDeviceId}
        onChange={(e) => {
          router.push(`/dashboard/alarms?deviceId=${encodeURIComponent(e.target.value)}`);
        }}
      >
        {devices.map((d) => (
          <option key={d.id} value={d.id}>
            {d.id}
          </option>
        ))}
      </select>
    </div>
  );
}
