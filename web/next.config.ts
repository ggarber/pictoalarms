import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  experimental: {
    ppr: true,
    clientSegmentCache: true,
    allowedDevOrigins: ['192.168.0.186']
  }
};

export default nextConfig;
