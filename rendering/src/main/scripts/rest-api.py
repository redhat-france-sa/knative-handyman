from http.server import BaseHTTPRequestHandler, HTTPServer
import bpy
import json
import sys

argv = sys.argv
argv = argv[argv.index("--") + 1:]

outputFolder=argv[0]
blendFolder=argv[1]

class handler(BaseHTTPRequestHandler):

    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type','application/json')
        self.end_headers()

        if (self.path == '/status'):
            message_dict = {'status': 'up','current-file': bpy.data.filepath}

        message = json.dumps(message_dict)
        self.wfile.write(bytes(message, "utf8"))

    def do_POST(self):
        content_len = int(self.headers.get('Content-Length'))
        post_body = self.rfile.read(content_len)
        input_dict = json.loads(post_body)

        if (self.path == '/open'):
            print("Opening file : " + json.dumps(input_dict))
            try:
                bpy.ops.wm.open_mainfile(filepath=blendFolder +"/"+ input_dict['fileName'])
                self.send_response(200)
                self.send_header('Content-type','application/json')
                self.end_headers()
                message_dict = {'status': 'ok'}
            except:
                self.send_response(500)
                self.send_header('Content-type','application/json')
                self.end_headers()
                message_dict = {'status': 'error'}



        if (self.path == '/render-region'):
            print("rendering region : " + json.dumps(input_dict))
            for scene in bpy.data.scenes:
                scene.render.resolution_percentage = 100
                scene.render.resolution_x = input_dict['resolutionX']
                scene.render.resolution_y = input_dict['resolutionY']
                scene.render.use_border = True
                scene.render.use_crop_to_border = True
                scene.render.border_min_x = float(input_dict['areaX'])/float(input_dict['frameDivider'])
                scene.render.border_min_y = float(input_dict['areaY'])/float(input_dict['frameDivider'])
                scene.render.border_max_x = float(input_dict['areaX']+1)/float(input_dict['frameDivider'])
                scene.render.border_max_y = float(input_dict['areaY']+1)/float(input_dict['frameDivider'])
                scene.render.image_settings.file_format='PNG'
                scene.render.image_settings.color_depth='16'
                scene.render.image_settings.color_mode='RGBA'
                scene.render.use_compositing=False

                scene.render.tile_x=16
                scene.render.tile_y=16

                scene.cycles.samples=input_dict['samples']
                scene.cycles.use_adaptive_sampling=True
                scene.cycles.use_denoising=False


                scene.view_settings.view_transform='Filmic Log'
                scene.view_settings.look='None'

                scene.render.use_save_buffers=True
                scene.render.use_persistent_data=False

                #scene.render.filepath = outputFolder + "/" + input_dict['outputPrefix'] +"_"+  str(input_dict['areaX']) + "_" + str(input_dict['areaY']) + ".png"
                outfname=input_dict['outputPrefix'] +"_"+ str(int(input_dict['frameDivider'])-input_dict['areaY']-1) + "_" + str(input_dict['areaX']) + ".png"
                scene.render.filepath = outputFolder + "/" + outfname
            bpy.ops.render.render(write_still=True)
            self.send_response(200)
            self.send_header('Content-type','application/json')
            self.end_headers()
            message_dict = {
                'status': 'ok',
                'minX': scene.render.border_min_x,
                'minY': scene.render.border_min_y,
                'maxX': scene.render.border_max_x,
                'maxY': scene.render.border_max_y,
                'filePath': outfname
                }

        message = json.dumps(message_dict)
        self.wfile.write(bytes(message, "utf8"))

with HTTPServer(('', 8000), handler) as server:
    server.serve_forever()
