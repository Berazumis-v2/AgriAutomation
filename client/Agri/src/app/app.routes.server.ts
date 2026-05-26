import { RenderMode, ServerRoute } from '@angular/ssr';

// Disable global prerendering for all routes because many routes include
// parameters (e.g. ':id') and require `getPrerenderParams` to prerender.
// Use client rendering for parameterized routes to avoid build-time errors.
export const serverRoutes: ServerRoute[] = [
  {
    path: '**',
    renderMode: RenderMode.Client
  }
];
